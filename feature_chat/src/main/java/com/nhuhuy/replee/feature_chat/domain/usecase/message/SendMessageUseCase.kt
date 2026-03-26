package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.core.data.utils.then
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_chat.domain.repository.PushNotificationRepository
import java.util.UUID
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val syncManager: SyncManager,
    private val pushNotificationRepository: PushNotificationRepository,
    private val workerScheduler: WorkerScheduler,
    private val conversationRepository: ConversationRepository,
) {
    suspend operator fun invoke(
        repliedMessage: Message? = null,
        senderId: String,
        receiverId: String,
        conversationId: String,
        text: String,
    ): NetworkResult<String> {
        val message = Message(
            messageId = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            content = text,
            status = MessageStatus.PENDING,
            sentAt = System.currentTimeMillis(),
            seen = false,
            type = MessageType.TEXT,
            repliedMessageId = repliedMessage?.messageId,
            repliedMessageContent = repliedMessage?.content,
            repliedMessageSenderId = repliedMessage?.senderId,
            repliedMessageType = repliedMessage?.type,
            repliedMessageRemoteUrl = repliedMessage?.remoteUrl
        )

        return messageRepository.sendMessage(message = message)
            .then {
                conversationRepository.updateMetadataConversation(message)
                    .onSuccess {
                        syncManager.updateConversationStatus(
                            conversationId = conversationId,
                            synced = true
                        )
                    }
                    .onFailure {
                        syncManager.updateConversationStatus(
                            conversationId = conversationId,
                            synced = false
                        )
                        workerScheduler.scheduleConversationSyncWorker()
                    }
            }
            .onSuccess {
                syncManager.updateMessageStatus(
                    messageId = message.messageId,
                    status = MessageStatus.SYNCED
                )
                pushNotificationRepository.pushNotification(message)
            }
            .onFailure {
                syncManager.updateMessageStatus(
                    messageId = message.messageId,
                    status = MessageStatus.FAILED
                )
                workerScheduler.scheduleMessageSyncWorker()
            }
    }
}
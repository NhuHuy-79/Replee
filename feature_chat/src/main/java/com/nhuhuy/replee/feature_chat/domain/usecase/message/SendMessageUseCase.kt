package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.domain.repository.PushNotificationRepository
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.error_handling.onFailure
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.core.sync.SyncManager
import java.util.UUID
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val syncManager: SyncManager,
    private val pushNotificationRepository: PushNotificationRepository,
    private val workerScheduler: WorkerScheduler,
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
            type = MessageType.TEXT,
            repliedMessageId = repliedMessage?.messageId,
            repliedMessageContent = repliedMessage?.content,
            repliedMessageSenderId = repliedMessage?.senderId,
            repliedMessageType = repliedMessage?.type,
            repliedMessageRemoteUrl = repliedMessage?.remoteUrl
        )

        return messageRepository.sendMessage(message = message)
            .onSuccess {
                pushNotificationRepository.pushNotification(message)
                syncManager.updateMessageStatus(
                    messageId = message.messageId,
                    status = MessageStatus.SYNCED
                )
                syncManager.updateConversationStatus(
                    conversationId = conversationId,
                    synced = true
                )
            }
            .onFailure {
                syncManager.updateMessageStatus(
                    messageId = message.messageId,
                    status = MessageStatus.FAILED
                )
                syncManager.updateConversationStatus(
                    conversationId = conversationId,
                    synced = false
                )
                workerScheduler.scheduleMessageSyncWorker()
                workerScheduler.scheduleConversationSyncWorker()
            }
    }
}

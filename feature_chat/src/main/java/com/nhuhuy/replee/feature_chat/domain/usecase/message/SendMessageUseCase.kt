package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.feature_chat.data.NotifyService
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import java.util.UUID
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val syncManager: SyncManager,
    private val notifyService: NotifyService
) {
    suspend operator fun invoke(
        senderId: String,
        receiverId: String,
        conversationId: String,
        text: String
    ): NetworkResult<String> {
        val message = Message(
            messageId = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            content = text,
            status = MessageStatus.PENDING,
            seen = false
        )
        return messageRepository.sendMessage(message = message)
            .onSuccess { messageId ->
                syncManager.updateMessageStatus(
                    messageId = messageId,
                    status = MessageStatus.SYNCED
                )
                notifyService.sendNotification(message)
            }
            .onFailure {
                syncManager.updateMessageStatus(
                    messageId = message.messageId,
                    status = MessageStatus.FAILED
                )
            }
    }
}
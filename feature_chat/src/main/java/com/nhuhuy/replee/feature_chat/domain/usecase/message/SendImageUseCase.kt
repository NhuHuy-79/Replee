package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import java.util.UUID
import javax.inject.Inject

class SendImageUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val messageRepository: MessageRepository,
) {
    suspend operator fun invoke(
        senderId: String,
        receiverId: String,
        uriPath: String,
        conversationId: String
    ): NetworkResult<String> {
        val raw = Message(
            messageId = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            status = MessageStatus.PENDING,
            content = "",
            sentAt = System.currentTimeMillis(),
            seen = false,
            localUriPath = uriPath,
            type = MessageType.IMAGE
        )

        return messageRepository.sendImage(rawMessage = raw, uriPath = uriPath)
            .onFailure {
                syncManager.updateMessageStatus(raw.messageId, MessageStatus.FAILED)
            }
            .onSuccess {
                syncManager.updateMessageStatus(raw.messageId, MessageStatus.SYNCED)
            }
    }
}
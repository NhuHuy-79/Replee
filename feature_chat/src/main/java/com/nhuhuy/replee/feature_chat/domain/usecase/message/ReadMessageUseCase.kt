package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class ReadMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String,
    ): NetworkResult<Unit> {
        return messageRepository.markMessagesAsRead(
            messageIds = messageIds,
            conversationId = conversationId,
            receiverId = receiverId
        )
    }
}
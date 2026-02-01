package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.core.common.error_handling.NetworkResult
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
            messageIds = listOf(),
            conversationId = conversationId,
            receiverId = receiverId
        )
    }
}
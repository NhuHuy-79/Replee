package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessagePositionUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(conversationId: String, messageId: String): Int {
        return messageRepository.getIndexOfMessage(
            conversationId = conversationId,
            messageId = messageId
        )
    }
}
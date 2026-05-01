package com.nhuhuy.replee.core.sync.usecase

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import javax.inject.Inject

class FetchNewMessagesSyncUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(conversationId: String): NetworkResult<Unit> {
        val timeStamp =
            messageRepository.getNewestMessageInConversation(conversationId)?.sentAt ?: 0L
        return messageRepository.fetchMessagesByTimestamp(
            conversationId = conversationId,
            timestamp = timeStamp
        )
    }
}

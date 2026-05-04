package com.nhuhuy.replee.core.sync.domain.usecase.message

import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
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

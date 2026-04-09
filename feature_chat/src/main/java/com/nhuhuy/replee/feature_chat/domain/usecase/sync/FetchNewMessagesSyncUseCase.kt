package com.nhuhuy.replee.feature_chat.domain.usecase.sync

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
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

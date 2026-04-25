package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class RemoveReactionUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        messageId: String,
        userId: String,
        reaction: String
    ): NetworkResult<Unit> {
        return repository.removeReaction(
            conversationId = conversationId,
            messageId = messageId,
            userId = userId,
            reaction = reaction
        )
    }
}

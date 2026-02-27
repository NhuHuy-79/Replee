package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import javax.inject.Inject

class GetConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        ownerId: String,
        otherUserId: String,
    ): NetworkResult<String> {
        return conversationRepository.getOrCreateConversation(
            ownerId = ownerId,
            otherUserId = otherUserId
        )
    }
}
package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
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

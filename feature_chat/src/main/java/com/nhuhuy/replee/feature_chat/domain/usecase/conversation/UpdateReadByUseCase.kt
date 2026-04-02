package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import javax.inject.Inject

class UpdateReadByUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String): NetworkResult<Unit> {
        return conversationRepository.updateReadBy(
            conversationId = conversationId,
            readBy = System.currentTimeMillis()
        )
    }
}
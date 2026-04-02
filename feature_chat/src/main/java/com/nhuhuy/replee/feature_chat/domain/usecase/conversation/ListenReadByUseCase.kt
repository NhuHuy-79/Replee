package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class ListenReadByUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(
        conversationId: String,
        receiverId: String,
    ): Flow<Long> {
        return conversationRepository.listenReadBy(
            conversationId = conversationId,
            receiverId = receiverId
        ).onEach { readBy ->
            conversationRepository.updateReadBy(conversationId, readBy)
        }
    }
}
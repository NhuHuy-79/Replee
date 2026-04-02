package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveReadByUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(conversationId: String): Flow<Long> {
        return conversationRepository.observeReadBy(conversationId)
    }
}
package com.nhuhuy.replee.feature_chat.domain.usecase.sync

import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(
        currentUserId: String,
        limit: Int
    ): Flow<Unit> {
        return conversationRepository.listenConversationWithLimit(
            ownerId = currentUserId,
            limit = limit
        )
    }
}
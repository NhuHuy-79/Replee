package com.nhuhuy.replee.core.sync.usecase.conversation

import com.nhuhuy.replee.core.domain.repository.ConversationRepository
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

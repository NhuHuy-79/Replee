package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ListenConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(
        ownerId: String,
        limit: Int,
    ): Flow<List<DataChange<Conversation>>> {
        return conversationRepository.listenConversationWithLimit(
            ownerId = ownerId,
            limit = limit
        )
    }
}
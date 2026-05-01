package com.nhuhuy.replee.feature_home.domain.usecase.conversation

import com.nhuhuy.replee.core.domain.repository.ConversationRepository
import com.nhuhuy.replee.core.model.chat.Conversation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLocalConversationListUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(ownerId: String): Flow<List<Conversation>> {
        return conversationRepository.observeLocalConversationList(ownerId)
    }
}

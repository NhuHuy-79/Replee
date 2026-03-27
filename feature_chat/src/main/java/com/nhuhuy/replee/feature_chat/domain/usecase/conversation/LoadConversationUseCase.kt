package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.replee.feature_chat.domain.model.converastion.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(ownerId: String): Flow<List<Conversation>> {
        return conversationRepository.observeLocalConversationList(ownerId)
    }
}
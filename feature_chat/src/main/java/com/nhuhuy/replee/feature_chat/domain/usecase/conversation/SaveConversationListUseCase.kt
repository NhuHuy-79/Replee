package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
import javax.inject.Inject

class SaveConversationListUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Conversation>> {
        return conversationRepository.fetchConversations()
    }
}

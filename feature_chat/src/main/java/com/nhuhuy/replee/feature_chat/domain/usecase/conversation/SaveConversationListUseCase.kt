package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import javax.inject.Inject

class SaveConversationListUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Conversation>> {
        return conversationRepository.fetchConversations()
    }
}
package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(): Flow<NetworkResult<List<Conversation>>> =
        conversationRepository.observeNetworkConversation()

}
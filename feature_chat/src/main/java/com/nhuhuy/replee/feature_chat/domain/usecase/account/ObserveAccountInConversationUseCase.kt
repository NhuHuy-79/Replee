package com.nhuhuy.replee.feature_chat.domain.usecase.account

import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAccountInConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(currentUserId: String): Flow<List<String>> {
        return conversationRepository.observeOtherUserInConversation(currentUserId)
    }
}
package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import javax.inject.Inject

class SaveConversationUserUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(ownerId: String) {
        return conversationRepository.fetchOtherUserInConversations(ownerId)
    }
}
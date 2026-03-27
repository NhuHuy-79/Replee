package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.feature_chat.domain.model.converastion.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import javax.inject.Inject

class LoadConversationInformationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String): Conversation {
        return conversationRepository.getConversationById(conversationId)
    }

}
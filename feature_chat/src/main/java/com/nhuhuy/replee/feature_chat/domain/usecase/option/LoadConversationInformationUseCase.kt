package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadConversationInformationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(conversationId: String): Flow<Conversation> {
        return conversationRepository.observeConversationById(conversationId)
    }

}
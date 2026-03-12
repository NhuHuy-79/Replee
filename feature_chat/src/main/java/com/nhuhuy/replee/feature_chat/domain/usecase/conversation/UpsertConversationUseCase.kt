package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import javax.inject.Inject

class UpsertConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(dataChanges: List<DataChange<Conversation>>) {
        return conversationRepository.updateLocalDataChange(dataChanges)
    }
}
package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.model.message.ChatAction
import com.nhuhuy.replee.feature_chat.domain.repository.ChatActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import javax.inject.Inject

class DeleteConversationUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val conversationRepository: ConversationRepository,
    private val chatActionRepository: ChatActionRepository
) {
    suspend operator fun invoke(conversationId: String): NetworkResult<Unit> {
        return conversationRepository.deleteConversation(conversationId)
            .onSuccess {
                syncManager.updateConversationStatus(conversationId, synced = true)
            }
            .onFailure {
                syncManager.updateConversationStatus(conversationId, synced = false)
                chatActionRepository.upsertAction(
                    ChatAction(
                        targetId = conversationId,
                        actionType = ActionType.DELETE_CONVERSATION
                    )
                )
            }
    }
}

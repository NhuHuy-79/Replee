package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.error_handling.onFailure
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.core.model.chat.ActionType
import com.nhuhuy.replee.core.sync.SyncManager
import com.nhuhuy.replee.core.model.chat.ChatAction
import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
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

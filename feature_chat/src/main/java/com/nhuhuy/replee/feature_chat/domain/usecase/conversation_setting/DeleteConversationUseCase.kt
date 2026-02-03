package com.nhuhuy.replee.feature_chat.domain.usecase.conversation_setting

import com.nhuhuy.replee.core.common.error_handling.NetworkResult
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import javax.inject.Inject

class DeleteConversationUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val conversationSettingRepository: ConversationSettingRepository
) {
    suspend operator fun invoke(conversationId: String): NetworkResult<Unit> {
        return conversationSettingRepository.deleteConversation(conversationId)
            .onSuccess {
                syncManager.updateConversationStatus(conversationId, synced = true)
            }
            .onFailure {
                syncManager.updateConversationStatus(conversationId, synced = false)
            }
    }
}
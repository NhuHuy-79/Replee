package com.nhuhuy.replee.feature_chat.domain.usecase.conversation_setting

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import javax.inject.Inject

class PinConversationUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val conversationSettingRepository: ConversationSettingRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        currentUserId: String,
        pinned: Boolean
    ): NetworkResult<Unit> {
        return conversationSettingRepository.pinConversation(
            conversationId = conversationId,
            currentUser = currentUserId,
            pinned = pinned
        ).onSuccess {
            syncManager.updateConversationStatus(conversationId, synced = true)
        }
            .onFailure {
                syncManager.updateConversationStatus(conversationId, synced = false)
            }
    }
}
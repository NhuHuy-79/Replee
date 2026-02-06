package com.nhuhuy.replee.feature_chat.domain.usecase.conversation_setting

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import javax.inject.Inject

class MuteUserUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val conversationSettingRepository: ConversationSettingRepository,
) {
    suspend operator fun invoke(
        conversationId: String,
        muted: Boolean,
        otherUserId: String
    ): NetworkResult<Unit> {
        return conversationSettingRepository.muteOtherUser(
            conversationId = conversationId,
            otherUser = otherUserId,
            muted = muted
        ).onSuccess {
            syncManager.updateConversationStatus(conversationId, synced = true)
        }
            .onFailure {
                syncManager.updateConversationStatus(conversationId, synced = false)
            }
    }
}
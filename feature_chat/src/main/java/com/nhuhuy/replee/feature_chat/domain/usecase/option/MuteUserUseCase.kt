package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.repository.OptionRepository
import javax.inject.Inject

class MuteUserUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val optionRepository: OptionRepository,
    private val workerScheduler: WorkerScheduler,
) {
    suspend operator fun invoke(
        conversationId: String,
        muted: Boolean,
        currentUserId: String
    ): NetworkResult<Unit> {
        return optionRepository.muteOtherUser(
            conversationId = conversationId,
            otherUser = currentUserId,
            muted = muted
        ).onSuccess {
            syncManager.updateConversationStatus(conversationId, synced = true)
        }
            .onFailure {
                syncManager.updateConversationStatus(conversationId, synced = false)
                workerScheduler.scheduleConversationSyncWorker()
            }
    }
}
package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.model.onFailure
import com.nhuhuy.replee.core.model.onSuccess
import com.nhuhuy.replee.core.sync.SyncManager
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.domain.repository.OptionRepository
import javax.inject.Inject

class PinConversationUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val optionRepository: OptionRepository,
    private val workerScheduler: WorkerScheduler
) {
    suspend operator fun invoke(
        conversationId: String,
        currentUserId: String,
        pinned: Boolean
    ): NetworkResult<Unit> {
        return optionRepository.pinConversation(
            conversationId = conversationId,
            currentUser = currentUserId,
            pinned = pinned
        ).onSuccess {
            syncManager.updateConversationStatus(conversationId, synced = true)
        }
            .onFailure {
                syncManager.updateConversationStatus(conversationId, synced = false)
                workerScheduler.scheduleConversationSyncWorker()
            }
    }
}

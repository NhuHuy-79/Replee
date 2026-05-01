package com.nhuhuy.replee.feature_chat.domain.usecase.option

import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.model.onFailure
import com.nhuhuy.replee.core.model.onSuccess
import com.nhuhuy.replee.core.sync.SyncManager
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.domain.repository.OptionRepository
import javax.inject.Inject

class UpdateOtherNickNameUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val workerScheduler: WorkerScheduler,
    private val optionRepository: OptionRepository
) {
    suspend operator fun invoke(
        uid: String,
        conversationId: String,
        nickName: String
    ): NetworkResult<Unit> {
        return optionRepository.updateOtherUserNickname(
            uid = uid,
            conversationId = conversationId,
            nickName = nickName
        ).onSuccess {
            syncManager.updateConversationStatus(conversationId, synced = true)
        }
            .onFailure {
                syncManager.updateConversationStatus(conversationId, synced = false)
                workerScheduler.scheduleConversationSyncWorker()
            }
    }
}

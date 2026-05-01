package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.model.onFailure
import com.nhuhuy.replee.core.model.onSuccess
import com.nhuhuy.replee.core.sync.SyncManager
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
import javax.inject.Inject

class UpdateUnreadMessageUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val conversationRepository: ConversationRepository,
    private val workerScheduler: WorkerScheduler,
) {
    suspend operator fun invoke(
        conversationId: String,
        receiverId: String,
    ): NetworkResult<Unit> {
        return conversationRepository.markAllMessagesRead(
            conversationId = conversationId,
            currentUserId = receiverId
        )
            .onFailure {
                syncManager.updateConversationStatus(
                    conversationId = conversationId,
                    synced = false
                )
                workerScheduler.scheduleConversationSyncWorker()
            }
            .onSuccess {
                syncManager.updateConversationStatus(conversationId = conversationId, synced = true)
            }
    }
}

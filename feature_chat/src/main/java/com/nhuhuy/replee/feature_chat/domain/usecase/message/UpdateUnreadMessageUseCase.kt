package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import javax.inject.Inject

class UpdateUnreadMessageUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val conversationRepository: ConversationRepository,
    private val workerScheduler: WorkerScheduler,
) {
    suspend operator fun invoke(
        conversationId: String,
        receiverId: String,
    ): NetworkResult<String> {
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
package com.nhuhuy.replee.worker.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.feature_chat.data.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class ConversationSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val parameters: WorkerParameters,
    private val syncManager: SyncManager,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            Timber.d("Start Sync Conversation To Firestore!")
            val resource = syncManager.syncConversation()

            resource.onFailure { error ->
                Timber.e("Failed to Sync Conversation To Firestore!")
                if (error is RemoteFailure.Network) {
                    Timber.e("Network Error!")
                    Result.retry()
                }
                Result.failure()
            }
            Timber.d("Sync Conversation To Firestore Success!")
            Result.success()
        }
    }
}
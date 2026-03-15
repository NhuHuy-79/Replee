package com.nhuhuy.replee.worker.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.data.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

object SyncKey {
    const val FAILURE = "Sync Failure"
    const val SUCCESS = "Sync Success"
}

@HiltWorker
class SyncMessageWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            if (runAttemptCount > 3) {
                return@withContext Result.failure()
            }

            Timber.d("Start Sync Message To Firestore!")
            val result = syncManager.syncMessage()
            when (result) {
                is NetworkResult.Success -> {
                    Timber.d("Sync Message To Firestore Success!")
                    Result.success()
                }

                is NetworkResult.Failure -> {
                    Timber.d("Failed to Sync Message To Firestore!")
                    Result.retry()
                }
            }
        }
    }
}
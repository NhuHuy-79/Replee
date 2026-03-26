package com.nhuhuy.replee.worker.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.data.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@HiltWorker
class SyncFileWorker @AssistedInject constructor(
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
            val result = syncManager.uploadFile()
            return@withContext when (result) {
                is NetworkResult.Success -> {
                    Result.success(
                        workDataOf(
                            SyncKey.SUCCESS to "Sync Success"
                        )
                    )
                }

                is NetworkResult.Failure -> {
                    Result.retry()
                }
            }
        }
    }
}
package com.nhuhuy.replee.core.sync.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.sync.usecase.FetchNewMessagesSyncUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@HiltWorker
class SaveNewMessageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fetchNewMessagesSyncUseCase: FetchNewMessagesSyncUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            if (runAttemptCount >= 5) {
                return@withContext Result.failure()
            }

            val conversationId = inputData.getString("conversationId")

            if (conversationId == null) return@withContext Result.failure()

            return@withContext when (fetchNewMessagesSyncUseCase(conversationId)) {
                is NetworkResult.Failure -> Result.retry()
                is NetworkResult.Success -> Result.success()
            }
        }
    }
}

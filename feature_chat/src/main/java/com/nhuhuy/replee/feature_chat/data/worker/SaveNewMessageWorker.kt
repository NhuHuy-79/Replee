package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.usecase.sync.FetchNewMessagesSyncUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@HiltWorker
class SaveNewMessageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fetchNewMessagesSyncUseCase: FetchNewMessagesSyncUseCase,
    private val ioDispatcher: CoroutineDispatcher
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

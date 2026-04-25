package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.utils.IoDispatcher
import com.nhuhuy.replee.feature_chat.domain.usecase.sync.DeleteMessagesSyncUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.sync.PinMessageSyncUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.sync.UnPinMessageSyncUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@HiltWorker
class SyncMessageActionWorker @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val deleteMessagesSyncUseCase: DeleteMessagesSyncUseCase,
    private val pinMessageSyncUseCase: PinMessageSyncUseCase,
    private val unPinMessageSyncUseCase: UnPinMessageSyncUseCase,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            if (runAttemptCount >= 5) return@withContext Result.failure()

            val deleteDeferred = async { deleteMessagesSyncUseCase() }
            val pinDeferred = async { pinMessageSyncUseCase() }
            val unpinDeferred = async { unPinMessageSyncUseCase() }

            val results = listOf(
                deleteDeferred.await(),
                pinDeferred.await(),
                unpinDeferred.await()
            )

            val isAnyFailed = results.any { result -> result is NetworkResult.Failure }

            if (isAnyFailed) {
                Result.retry()
            } else {
                Result.success()
            }
        }
    }
}

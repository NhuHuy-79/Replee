package com.nhuhuy.replee.core.sync.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.sync.domain.usecase.account.UploadAvatarSyncUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val KEY_UID = "worker_params_uid"

@HiltWorker
class UploadAvatarWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val uploadAvatarSyncUseCase: UploadAvatarSyncUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            val uid = inputData.getString(KEY_UID) ?: return@withContext Result.failure()

            if (runAttemptCount >= 5) {
                return@withContext Result.failure()
            }

            return@withContext when (uploadAvatarSyncUseCase(uid)) {
                is NetworkResult.Failure -> Result.retry()
                is NetworkResult.Success -> Result.success()
            }
        }
    }
}

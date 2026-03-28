package com.nhuhuy.replee.feature_profile.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.core.domain.repository.FileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class UploadAvatarWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val fileRepository: FileRepository,
    private val accountRepository: AccountRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            val uid = inputData.getString(KEY_UID) ?: return@withContext Result.failure()
            val localFilePath = fileRepository.getUriPathWithUserId(uid)

            if (localFilePath == null) {
                Timber.e("Cannot found local file path!")
                return@withContext Result.failure()
            }

            if (runAttemptCount >= 5) {
                return@withContext Result.failure()
            }

            val result = fileRepository.uploadImageWithOption(
                uriPath = localFilePath.localPath,
                folder = "replee/avatars",
                option = mapOf(
                    "public_id" to "avatar_${uid}",
                    "overwrite" to "true",
                    "quality" to "auto",
                    "fetch_format" to "auto",
                    "transformation" to "w_500,h_500,c_fill,g_face"
                )
            )

            when (result) {
                is NetworkResult.Failure -> {
                    return@withContext Result.retry()
                }

                is NetworkResult.Success -> {
                    accountRepository.updateUserImage(uid = uid, remoteUrl = result.data)
                        .onSuccess {
                            return@withContext Result.success()
                        }
                        .onFailure {
                            return@withContext Result.retry()
                        }
                }
            }

            Result.success()
        }
    }
}
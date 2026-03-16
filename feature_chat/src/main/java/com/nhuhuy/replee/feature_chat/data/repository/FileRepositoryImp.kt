package com.nhuhuy.replee.feature_chat.data.repository

import android.content.Context
import androidx.core.net.toUri
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.UploadFileState
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.core.domain.repository.ValidateFileResult
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.network.quailify.Ktor
import com.nhuhuy.replee.feature_chat.data.worker.UploadFileWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val MESSAGE_ID_INPUT = "message_id_input"
const val URI_PATH_INPUT = "uri_path_input"


class FileRepositoryImp @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @Ktor private val uploadFileService: UploadFileService,
) : FileRepository {
    override suspend fun validateFileSize(uriPath: String): ValidateFileResult {
        return withContext(ioDispatcher) {
            val uri = uriPath.toUri()
            val maxSize = 100 * 1024 * 1024
            val fileSize = context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
                it.length
            } ?: 0L

            if (fileSize > maxSize) return@withContext ValidateFileResult.FileTooLarge
            return@withContext ValidateFileResult.Valid
        }
    }

    override suspend fun uploadFile(uriPath: String): NetworkResult<String> {
        return execute(dispatcher = ioDispatcher) {
            uploadFileService.uploadImageWithUriPath(uriPath)
        }
    }

    override suspend fun scheduleUploadFile(messageId: String, uriPath: String) {
        val uri = uriPath.toUri()
        val uploadDir = File(context.cacheDir, "upload")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs() // Lệnh này sẽ tạo thư mục /upload
        }
        val tempFile = File(uploadDir, "${messageId}.jpg")
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            return
        }

        val input = workDataOf(
            MESSAGE_ID_INPUT to messageId,
            URI_PATH_INPUT to tempFile.absolutePath
        )

        val uploadRequest = OneTimeWorkRequestBuilder<UploadFileWorker>()
            .setInputData(inputData = input)
            .setConstraints(
                constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = WorkRequest.MIN_BACKOFF_MILLIS,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setExpedited(policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "upload_$messageId",
            ExistingWorkPolicy.KEEP,
            uploadRequest
        )
    }

    override fun observeUploadFile(messageId: String, uriPath: String): Flow<UploadFileState> {
        TODO("Not implemented")
    }

}
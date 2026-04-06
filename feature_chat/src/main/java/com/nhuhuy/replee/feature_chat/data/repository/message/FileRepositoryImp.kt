package com.nhuhuy.replee.feature_chat.data.repository.message

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
import com.nhuhuy.core.domain.model.FilePath
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.UploadFileState
import com.nhuhuy.core.domain.model.ValidateFileResult
import com.nhuhuy.core.domain.repository.FileMetadata
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.core.data.data_source.FileStorageDataSource
import com.nhuhuy.replee.core.data.data_source.FileValidator
import com.nhuhuy.replee.core.data.data_source.file_path.FilePathLocalDataSource
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.network.quailify.Retrofit
import com.nhuhuy.replee.feature_chat.data.worker.SendFileWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val MESSAGE_ID_INPUT = "message_id_input"
const val URI_PATH_INPUT = "uri_path_input"


class FileRepositoryImp @Inject constructor(
    private val filePathLocalDataSourceImp: FilePathLocalDataSource,
    private val fileValidator: FileValidator,
    private val fileStorageDataSource: FileStorageDataSource,
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    @Retrofit private val uploadFileService: UploadFileService,
) : FileRepository {
    override suspend fun uploadImageWithOption(
        uriPath: String,
        folder: String,
        option: Map<String, String>
    ): NetworkResult<String> {
        return execute {
            val url = uploadFileService.uploadImageWithOption(
                uriPath = uriPath,
                folder = folder,
                option = option
            )

            url
        }
    }

    override suspend fun getUriPathWithUserId(userId: String): FilePath? {
        return withContext(ioDispatcher) {
            filePathLocalDataSourceImp.getFilePathByUserId(userId)
        }
    }

    override suspend fun getUriPathWithMessageId(messageId: String): FilePath? {
        return withContext(ioDispatcher) {
            filePathLocalDataSourceImp.getFilePathByMessageId(messageId)
        }
    }

    override suspend fun upsertFilePath(filePath: FilePath) {
        return withContext(ioDispatcher) {
            filePathLocalDataSourceImp.upsertFilePath(filePath)
        }
    }

    override suspend fun saveFileToInternalStorage(uriPath: String): String {
        return withContext(ioDispatcher) {
            fileStorageDataSource.saveToInternalStorage(
                uri = uriPath.toUri()
            )
        }
    }

    override suspend fun validateFileSize(uriPath: String): ValidateFileResult {
        return withContext(ioDispatcher) {
            fileValidator.validate(uriPath)
        }
    }

    override suspend fun uploadFile(uriPath: String): NetworkResult<String> {
        return execute(dispatcher = ioDispatcher) {
            uploadFileService.uploadImageWithUriPath(uriPath)
        }
    }

    override suspend fun scheduleUploadFile(messageId: String, uriPath: String) {

        //Scheduler now only take a messageId for do Work and needn't take a uriPath

        val uri = uriPath.toUri()
        val uploadDir = File(context.cacheDir, "upload")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
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

        val uploadRequest = OneTimeWorkRequestBuilder<SendFileWorker>()
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

    override suspend fun getFileMetadata(uriPath: String): FileMetadata {
        return withContext(ioDispatcher) {
            fileStorageDataSource.getFileMetadata(uriPath)
        }

    }

}
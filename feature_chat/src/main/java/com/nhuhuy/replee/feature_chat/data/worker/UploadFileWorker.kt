package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.repository.MESSAGE_ID_INPUT
import com.nhuhuy.replee.feature_chat.data.repository.URI_PATH_INPUT
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class UploadFileWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sessionManager: SessionManager,
    private val fileRepository: FileRepository,
    private val messageRepository: MessageRepository,
    private val syncManager: SyncManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val messageId = inputData.getString(MESSAGE_ID_INPUT) ?: return@withContext Result.retry()
        val uriPath = inputData.getString(URI_PATH_INPUT) ?: return@withContext Result.retry()
        val file = File(uriPath)


        if (runAttemptCount > 5) {
            cleanup(file)
            syncManager.updateMessageStatus(messageId = messageId, status = MessageStatus.FAILED)
            return@withContext Result.failure()
        }

        sessionManager.getUserIdOrNull() ?: return@withContext Result.failure()


        //TODO("Need check message is Exist or not)

        val uploadFileResult = fileRepository.uploadFile(uriPath)

        return@withContext when (uploadFileResult) {
            is NetworkResult.Failure -> Result.retry()
            is NetworkResult.Success -> {
                val remoteUrl = uploadFileResult.data
                val message = messageRepository.updateRemoteUrlMessage(
                    messageId = messageId,
                    remoteUrl = remoteUrl,
                    status = MessageStatus.PENDING
                ) ?: return@withContext Result.retry()


                when (messageRepository.sendMessage(message)) {
                    is NetworkResult.Success -> {
                        syncManager.updateMessageStatus(
                            messageId = messageId,
                            status = MessageStatus.SYNCED
                        )
                        cleanup(file)
                        Result.success()
                    }

                    is NetworkResult.Failure -> {
                        Result.retry()
                    }
                }
            }
        }
    }

    private fun cleanup(file: File) {
        if (file.exists()) file.delete()
    }
}
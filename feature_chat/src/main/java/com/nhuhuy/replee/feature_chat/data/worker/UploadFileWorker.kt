package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.repository.MESSAGE_ID_INPUT
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class UploadFileWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sessionManager: SessionManager,
    private val fileRepository: FileRepository,
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val workerScheduler: WorkerScheduler,
    private val syncManager: SyncManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val messageId = inputData.getString(MESSAGE_ID_INPUT) ?: return@withContext Result.retry()

        if (runAttemptCount >= 5) {
            syncManager.updateMessageStatus(messageId = messageId, status = MessageStatus.FAILED)
            return@withContext Result.failure()
        }

        val filePath = fileRepository.getUriPathWithMessageId(messageId)

        if (filePath == null) {
            Timber.e("Uri Path is null")
            return@withContext Result.failure()
        }

        sessionManager.getUserIdOrNull() ?: return@withContext Result.failure()

        val uploadFileResult = fileRepository.uploadFile(uriPath = filePath.localPath)

        return@withContext when (uploadFileResult) {
            is NetworkResult.Failure -> Result.retry()
            is NetworkResult.Success -> {

                val remoteUrl = uploadFileResult.data
                Timber.d("RemoteURL : $remoteUrl")
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

                        conversationRepository.updateMetadataConversation(message)
                            .onSuccess {
                                syncManager.updateConversationStatus(
                                    conversationId = message.conversationId,
                                    synced = true
                                )
                            }
                            .onFailure {
                                syncManager.updateConversationStatus(
                                    conversationId = message.conversationId,
                                    synced = false
                                )
                                workerScheduler.scheduleConversationSyncWorker()
                            }

                        Result.success()
                    }

                    is NetworkResult.Failure -> {
                        Result.retry()
                    }
                }
            }
        }
    }
}
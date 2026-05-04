package com.nhuhuy.replee.core.sync.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.domain.worker.MESSAGE_ID_INPUT
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.sync.SyncManager
import com.nhuhuy.replee.core.sync.domain.usecase.message.SendFileSyncUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SendFileWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sendFileSyncUseCase: SendFileSyncUseCase,
    private val workerScheduler: WorkerScheduler,
    private val syncManager: SyncManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val messageId = inputData.getString(MESSAGE_ID_INPUT) ?: return@withContext Result.retry()

        if (runAttemptCount >= 5) {
            syncManager.updateMessageStatus(messageId = messageId, status = MessageStatus.FAILED)
            return@withContext Result.failure()
        }

        val result = sendFileSyncUseCase(
            messageId = messageId,
            onUpdateStatus = { id, status ->
                syncManager.updateMessageStatus(id, status)
            },
            onSyncConversationFailure = {
                workerScheduler.scheduleConversationSyncWorker()
            }
        )

        return@withContext when (result) {
            is NetworkResult.Failure -> Result.retry()
            is NetworkResult.Success -> {
                Result.success()
            }
        }
    }
}

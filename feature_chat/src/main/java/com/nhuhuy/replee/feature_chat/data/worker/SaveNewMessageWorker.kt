package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveNewMessageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val messageRepository: MessageRepository,
    private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            if (runAttemptCount >= 5) {
                return@withContext Result.failure()
            }

            val conversationId = inputData.getString("conversationId")

            if (conversationId == null) return@withContext Result.failure()

            val timeStamp =
                messageRepository.getNewestMessageInConversation(conversationId)?.sentAt ?: 0L
            val result = messageRepository.fetchMessagesByTimestamp(
                conversationId = conversationId,
                timestamp = timeStamp
            )

            return@withContext when (result) {
                is NetworkResult.Failure -> Result.retry()
                is NetworkResult.Success -> Result.success()
            }
        }
    }

}
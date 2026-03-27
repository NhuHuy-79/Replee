package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction
import com.nhuhuy.replee.feature_chat.domain.repository.ActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DeleteMessageWorker @AssistedInject constructor(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val actionRepository: ActionRepository,
    private val messageRepository: MessageRepository,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            if (runAttemptCount >= 5) {
                return@withContext Result.failure()
            }

            val action =
                actionRepository.getDeletedActions().filterIsInstance<MessageAction.Delete>()
            if (action.isEmpty()) {
                return@withContext Result.success()
            }

            val messagesIds: List<String> = action.map { delete -> delete.messageId }
            val messages = messageRepository.getMessageListById(messagesIds)

            if (messagesIds.isEmpty()) {
                return@withContext Result.success()
            }

            val deleteResult = messageRepository.deleteMultipleMessage(messages)
            return@withContext when (deleteResult) {
                is NetworkResult.Failure -> {
                    Result.retry()
                }

                is NetworkResult.Success -> {
                    actionRepository.markActionAsSynced(MessageAction.Delete())
                    Result.success()
                }
            }
        }
    }
}
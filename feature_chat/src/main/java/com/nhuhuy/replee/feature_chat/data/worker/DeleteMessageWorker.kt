package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.domain.repository.MessageActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DeleteMessageWorker @AssistedInject constructor(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val messageActionRepository: MessageActionRepository,
    private val messageRepository: MessageRepository,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            if (runAttemptCount >= 5) {
                return@withContext Result.failure()
            }

            //Get Delete Action List
            val messageActions =
                messageActionRepository.getActionListWithType(type = ActionType.DELETE)
            if (messageActions.isEmpty()) {
                return@withContext Result.success()
            }

            //Get Messages In Local
            val messagesIds: List<String> = messageActions.map { delete -> delete.targetId }
            val messages = messageRepository.getMessageListById(messagesIds)

            if (messagesIds.isEmpty()) {
                return@withContext Result.success()
            }

            //Delete Multiple Message
            val deleteResult = messageRepository.deleteMultipleMessage(messages)
            return@withContext when (deleteResult) {
                is NetworkResult.Failure -> {
                    Result.retry()
                }

                is NetworkResult.Success -> {
                    val actionIds = messageActions.map { action -> action.id }
                    messageActionRepository.deleteMessageActionListById(actionIds = actionIds)
                    Result.success()
                }
            }
        }
    }
}
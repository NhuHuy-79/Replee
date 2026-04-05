package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.replee.feature_chat.data.worker.MessageWorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction
import com.nhuhuy.replee.feature_chat.domain.repository.ActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val actionRepository: ActionRepository,
    private val scheduler: MessageWorkerScheduler,
) {
    suspend operator fun invoke(message: Message): NetworkResult<String> {
        return messageRepository.deleteMessage(message)
            .onFailure {
                val deleteAction = MessageAction.Delete(message.messageId)
                actionRepository.upsertAction(deleteAction)
                scheduler.scheduleMessageSyncWorker(
                    messageId = message.messageId,
                    conversationId = message.conversationId
                )
            }
    }
}
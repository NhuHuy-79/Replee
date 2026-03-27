package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.core.data.utils.then
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.worker.MessageWorkerScheduler
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.repository.ActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val actionRepository: ActionRepository,
    private val syncManager: SyncManager,
    private val scheduler: MessageWorkerScheduler,
) {
    suspend operator fun invoke(message: Message): NetworkResult<String> {
        return messageRepository.deleteMessage(message)
            .then { messageId ->
                conversationRepository.deleteMetadataLastMessage(message)
                    .onSuccess { conversationId ->
                        syncManager.updateConversationStatus(
                            conversationId = conversationId,
                            synced = true
                        )
                    }
                    .onFailure {
                        syncManager.updateConversationStatus(
                            conversationId = message.conversationId,
                            synced = false
                        )
                        scheduler.scheduleMessageSyncWorker(
                            messageId = message.messageId,
                            conversationId = message.conversationId
                        )
                    }
            }
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
package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction
import com.nhuhuy.replee.feature_chat.domain.repository.MessageActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class PinMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val workerScheduler: WorkerScheduler,
    private val messageActionRepository: MessageActionRepository
) {
    suspend operator fun invoke(message: Message) {
        messageRepository.updatePinStatusMessage(
            conversationId = message.conversationId,
            messageId = message.messageId,
            pinned = true
        ).onFailure {
            val pinAction = MessageAction(
                actionType = ActionType.UNPIN,
                targetId = message.messageId,
            )
            messageActionRepository.upsertAction(pinAction)
            workerScheduler.scheduleMessageActionWorker()
        }
    }
}
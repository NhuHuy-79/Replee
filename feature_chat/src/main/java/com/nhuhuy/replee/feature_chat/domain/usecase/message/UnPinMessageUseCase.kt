package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.model.message.ChatAction
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.repository.ChatActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class UnPinMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val workerScheduler: WorkerScheduler,
    private val chatActionRepository: ChatActionRepository
) {
    suspend operator fun invoke(message: Message) {
        messageRepository.updatePinStatusMessage(
            conversationId = message.conversationId,
            messageId = message.messageId,
            pinned = false
        ).onFailure {
            val pinAction = ChatAction(
                actionType = ActionType.UNPIN,
                targetId = message.messageId,
            )
            chatActionRepository.upsertAction(pinAction)
            workerScheduler.scheduleMessageActionWorker()
        }
    }
}
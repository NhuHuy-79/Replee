package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.core.model.onFailure
import com.nhuhuy.replee.core.model.ActionType
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.model.ChatAction
import com.nhuhuy.replee.core.model.Message
import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import javax.inject.Inject

class PinMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val workerScheduler: WorkerScheduler,
    private val chatActionRepository: ChatActionRepository
) {
    suspend operator fun invoke(message: Message) {
        messageRepository.updatePinStatusMessage(
            conversationId = message.conversationId,
            messageId = message.messageId,
            pinned = true
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

package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction
import com.nhuhuy.replee.feature_chat.domain.repository.MessageActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

//Soft Delete Message => Call Worker to delete message from server and local
class DeleteMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val messageActionRepository: MessageActionRepository,
    private val scheduler: WorkerScheduler,
) {
    suspend operator fun invoke(message: Message) {
        messageRepository.deleteMessage(message)

        val deleteAction = MessageAction(
            actionType = ActionType.DELETE,
            targetId = message.messageId,
            payload = message.content
        )
        messageActionRepository.upsertAction(deleteAction)
        scheduler.scheduleDeleteMessage(
            message = message
        )
    }
}
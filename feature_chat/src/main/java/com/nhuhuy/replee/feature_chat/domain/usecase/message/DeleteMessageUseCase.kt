package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.core.model.ActionType
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.model.ChatAction
import com.nhuhuy.replee.core.model.Message
import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import javax.inject.Inject

//Soft Delete Message => Call Worker to delete message from server and local
class DeleteMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val chatActionRepository: ChatActionRepository,
    private val scheduler: WorkerScheduler,
) {
    suspend operator fun invoke(message: Message) {
        messageRepository.deleteMessage(message)

        val deleteAction = ChatAction(
            actionType = ActionType.DELETE,
            targetId = message.messageId,
            payload = message.content
        )
        chatActionRepository.upsertAction(deleteAction)
        scheduler.scheduleMessageActionWorker()
    }
}

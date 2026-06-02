package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.domain.repository.PushNotificationRepository
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.model.chat.ActionType
import com.nhuhuy.replee.core.model.chat.ChatAction
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.error_handling.onFailure
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import javax.inject.Inject

class AddReactionUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val chatActionRepository: ChatActionRepository,
    private val workerScheduler: WorkerScheduler,
    private val notificationRepository: PushNotificationRepository,
) {
    suspend operator fun invoke(
        message: Message,
        userId: String,
        reaction: String
    ): NetworkResult<Unit> {
        return messageRepository.addReaction(
            conversationId = message.conversationId,
            messageId = message.messageId,
            userId = userId,
            reaction = reaction
        )
            .onSuccess { notificationRepository.pushNotification(message) }
            .onFailure {
                val newMessageAction = ChatAction(
                    targetId = message.messageId,
                    actionType = ActionType.UPDATE_REACTION,
                    payload = reaction
                )

                chatActionRepository.upsertAction(newMessageAction)
                workerScheduler.scheduleMessageActionWorker()
            }
    }
}

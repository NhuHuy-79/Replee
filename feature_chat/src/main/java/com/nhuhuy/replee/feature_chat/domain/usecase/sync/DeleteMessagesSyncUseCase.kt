package com.nhuhuy.replee.feature_chat.domain.usecase.sync

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.domain.repository.ChatActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class DeleteMessagesSyncUseCase @Inject constructor(
    private val chatActionRepository: ChatActionRepository,
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        // Get Delete Action List
        val messageActions = chatActionRepository.getActionListWithType(type = ActionType.DELETE)
        if (messageActions.isEmpty()) {
            return NetworkResult.Success(Unit)
        }

        // Get Messages In Local
        val messagesIds: List<String> = messageActions.map { it.targetId }
        val messages = messageRepository.getMessageListById(messagesIds)

        if (messages.isEmpty()) {
            // If actions exist but messages are gone, clean up actions
            val actionIds = messageActions.map { it.id }
            chatActionRepository.deleteMessageActionListById(actionIds)
            return NetworkResult.Success(Unit)
        }

        // Delete Multiple Message on Network
        return when (val deleteResult = messageRepository.deleteMultipleMessage(messages)) {
            is NetworkResult.Failure -> deleteResult
            is NetworkResult.Success -> {
                val actionIds = messageActions.map { it.id }
                chatActionRepository.deleteMessageActionListById(actionIds = actionIds)
                NetworkResult.Success(Unit)
            }
        }
    }
}

package com.nhuhuy.replee.core.sync.domain.usecase.message

import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.model.chat.ActionType
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.sync.domain.MessageSyncRepository
import javax.inject.Inject

class DeleteMessagesSyncUseCase @Inject constructor(
    private val chatActionRepository: ChatActionRepository,
    private val messageRepository: MessageRepository,
    private val messageSyncRepository: MessageSyncRepository,
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        val messageActions = chatActionRepository.getActionListWithType(type = ActionType.DELETE)
        if (messageActions.isEmpty()) {
            return NetworkResult.Success(Unit)
        }

        val messagesIds: List<String> = messageActions.map { it.targetId }
        val messages = messageRepository.getMessageListById(messagesIds)

        if (messages.isEmpty()) {
            val actionIds = messageActions.map { it.id }
            chatActionRepository.deleteMessageActionListById(actionIds)
            return NetworkResult.Success(Unit)
        }

        return when (val deleteResult = messageSyncRepository.deleteMessages(messages)) {
            is NetworkResult.Failure -> deleteResult
            is NetworkResult.Success -> {
                val actionIds = messageActions.map { it.id }
                chatActionRepository.deleteMessageActionListById(actionIds = actionIds)
                deleteResult
            }
        }
    }
}

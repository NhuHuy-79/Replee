package com.nhuhuy.replee.core.sync.usecase

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.core.model.chat.ActionType
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import javax.inject.Inject

class UpdateReactionSyncUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val chatActionRepository: ChatActionRepository,
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        val messageActions =
            chatActionRepository.getActionListWithType(type = ActionType.UPDATE_REACTION)
        if (messageActions.isEmpty()) return NetworkResult.Success(Unit)

        val messageIds: List<String> = messageActions.map { action -> action.targetId }.distinct()
        val messages: List<Message> = messageRepository.getMessageListById(messageIds)

        return messageRepository.updateReactionMultiMessage(messages)
            .onSuccess {
                chatActionRepository.deleteMessageActionListById(
                    actionIds = messageActions.map { it.id }
                )
            }
    }
}

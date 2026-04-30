package com.nhuhuy.replee.feature_chat.domain.usecase.sync

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class UpdateReactionSyncUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val messageActionRepository: MessageActionRepository,
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        val messageActions =
            messageActionRepository.getActionListWithType(type = ActionType.UPDATE_REACTION)
        if (messageActions.isEmpty()) return NetworkResult.Success(Unit)

        val messageIds: List<String> = messageActions.map { action -> action.targetId }.distinct()
        val messages: List<Message> = messageRepository.getMessageListById(messageIds)

        return messageRepository.updateReactionMultiMessage(messages)
            .onSuccess {
                messageActionRepository.deleteMessageActionListById(
                    actionIds = messageActions.map { it.id }
                )
            }
    }
}

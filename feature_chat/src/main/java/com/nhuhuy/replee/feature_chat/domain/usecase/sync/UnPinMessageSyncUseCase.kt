package com.nhuhuy.replee.feature_chat.domain.usecase.sync

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.domain.repository.MessageActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class UnPinMessageSyncUseCase @Inject constructor(
    private val messageActionRepository: MessageActionRepository,
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        val messageActions = messageActionRepository.getActionListWithType(type = ActionType.UNPIN)
        val messageIds: List<String> = messageActions.map { action -> action.targetId }
        val messages = messageRepository.getMessageListById(messageIds)

        return messageRepository.pinMultipleRemoteMessage(
            messages = messages,
            pinned = false
        )
            .onSuccess {
                messageActionRepository.deleteMessageActionListById(
                    actionIds = messageActions.map { it.id }
                )
            }
    }
}
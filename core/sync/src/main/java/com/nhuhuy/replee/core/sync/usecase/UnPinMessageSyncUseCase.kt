package com.nhuhuy.replee.core.sync.usecase

import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.model.onSuccess
import com.nhuhuy.replee.core.model.ActionType
import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import javax.inject.Inject

class UnPinMessageSyncUseCase @Inject constructor(
    private val chatActionRepository: ChatActionRepository,
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        val messageActions = chatActionRepository.getActionListWithType(type = ActionType.UNPIN)
        val messageIds: List<String> = messageActions.map { action -> action.targetId }
        val messages = messageRepository.getMessageListById(messageIds)

        return messageRepository.pinMultipleRemoteMessage(
            messages = messages,
            pinned = false
        )
            .onSuccess {
                chatActionRepository.deleteMessageActionListById(
                    actionIds = messageActions.map { it.id }
                )
            }
    }
}

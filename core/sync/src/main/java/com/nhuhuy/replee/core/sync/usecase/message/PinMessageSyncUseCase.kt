package com.nhuhuy.replee.core.sync.usecase.message

import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.model.chat.ActionType
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.core.sync.domain.MessageSyncRepository
import javax.inject.Inject

class PinMessageSyncUseCase @Inject constructor(
    private val chatActionRepository: ChatActionRepository,
    private val messageRepository: MessageRepository,
    private val syncMessageRepository: MessageSyncRepository,
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        val messageActions = chatActionRepository.getActionListWithType(type = ActionType.PIN)
        val messageIds: List<String> = messageActions.map { action -> action.targetId }
        val messages = messageRepository.getMessageListById(messageIds)

        return syncMessageRepository.pinMessages(messages = messages, pinned = true)
            .onSuccess {
                chatActionRepository.deleteMessageActionListById(
                    actionIds = messageActions.map { it.id }
                )
            }
    }
}

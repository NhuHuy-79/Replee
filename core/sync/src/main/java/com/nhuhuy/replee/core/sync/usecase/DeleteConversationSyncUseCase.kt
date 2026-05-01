package com.nhuhuy.replee.core.sync.usecase

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.chat.ActionType
import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
import javax.inject.Inject

class DeleteConversationSyncUseCase @Inject constructor(
    private val chatActionRepository: ChatActionRepository,
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        val actions = chatActionRepository.getActionListWithType(ActionType.DELETE_CONVERSATION)
        if (actions.isEmpty()) return NetworkResult.Success(Unit)

        val conversationIds = actions.map { it.targetId }

        return when (val result =
            conversationRepository.deleteMultipleConversations(conversationIds)) {
            is NetworkResult.Failure -> result
            is NetworkResult.Success -> {
                chatActionRepository.deleteMessageActionListById(actions.map { it.id })
                NetworkResult.Success(Unit)
            }
        }
    }
}

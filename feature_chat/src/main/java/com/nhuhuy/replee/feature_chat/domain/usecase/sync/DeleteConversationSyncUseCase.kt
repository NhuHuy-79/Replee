package com.nhuhuy.replee.feature_chat.domain.usecase.sync

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.domain.repository.ChatActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
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

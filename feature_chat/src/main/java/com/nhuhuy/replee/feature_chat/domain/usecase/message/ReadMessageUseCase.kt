package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.core.common.utils.then
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import javax.inject.Inject

class ReadMessageUseCase @Inject constructor(
    private val syncManager: SyncManager,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String,
    ): NetworkResult<String> {
        return conversationRepository.markAllMessagesRead(
            conversationId = conversationId,
            currentUserId = receiverId
        ).then { conversationId ->
            messageRepository.markAllMessagesRead(
                conversationId = conversationId,
                receiverId = receiverId
            )
        }
            .onFailure {
                syncManager.updateConversationStatus(
                    conversationId = conversationId,
                    synced = false
                )
            }
            .onSuccess {
                syncManager.updateConversationStatus(conversationId = conversationId, synced = true)
            }
    }
}
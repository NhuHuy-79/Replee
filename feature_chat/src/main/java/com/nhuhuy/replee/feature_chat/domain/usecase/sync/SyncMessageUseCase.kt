package com.nhuhuy.replee.feature_chat.domain.usecase.sync

import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: String): Flow<Unit> {
        return messageRepository.listenMessageChanges(conversationId)
    }
}
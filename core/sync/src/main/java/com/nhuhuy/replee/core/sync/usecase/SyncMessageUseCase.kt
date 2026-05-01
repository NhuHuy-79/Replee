package com.nhuhuy.replee.core.sync.usecase

import com.nhuhuy.replee.core.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: String): Flow<Unit> {
        return messageRepository.listenMessageChanges(conversationId)
    }
}

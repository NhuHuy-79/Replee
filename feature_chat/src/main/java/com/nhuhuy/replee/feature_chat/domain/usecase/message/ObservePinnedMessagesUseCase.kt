package com.nhuhuy.replee.feature_chat.domain.usecase.message

import androidx.paging.PagingData
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.model.chat.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePinnedMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(
        conversationId: String,
        currentUserId: String
    ): Flow<PagingData<Message>> {
        return messageRepository.observePinnedMessages(
            conversationId = conversationId,
            currentUserId = currentUserId
        )
    }
}
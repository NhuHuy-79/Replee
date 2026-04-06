package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(
        conversationId: String,
        query: String,
    ): Flow<List<Message>> {
        return messageRepository.observeLocalMessagesWithQuery(
            conversationId = conversationId,
            query = query
        )
    }
}
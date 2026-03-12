package com.nhuhuy.replee.feature_chat.domain.usecase.message

import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListenMessageWithPagingUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(
        conversationId: String,
        limit: Int = 3
    ): Flow<List<DataChange<Message>>> {
        return messageRepository.observeMessageChangeWithPaging(conversationId, limit)
    }
}
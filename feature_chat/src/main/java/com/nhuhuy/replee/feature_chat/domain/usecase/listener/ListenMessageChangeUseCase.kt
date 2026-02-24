package com.nhuhuy.replee.feature_chat.domain.usecase.listener

import com.nhuhuy.replee.core.firebase.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListenMessageChangeUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: String): Flow<List<DataChange<Message>>> {
        return messageRepository.observeNetworkMessageChange(conversationId)
    }
}
package com.nhuhuy.replee.feature_chat.domain.usecase.message

import androidx.paging.PagingData
import com.nhuhuy.replee.feature_chat.domain.model.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLocalMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(conversationId: String): Flow<PagingData<LocalPathMessage>> {
        return messageRepository.observeLocalMessageWithPaging(conversationId)
    }
}
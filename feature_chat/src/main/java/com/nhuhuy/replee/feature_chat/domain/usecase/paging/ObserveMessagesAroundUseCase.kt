package com.nhuhuy.replee.feature_chat.domain.usecase.paging

import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessagesAroundUseCase @Inject constructor(
    private val paginatorRepository: PaginatorRepository
) {
    operator fun invoke(
        conversationId: String,
        anchorMessageId: String,
        limit: Int
    ): Flow<List<LocalPathMessage>> {
        return paginatorRepository.observeLocalMessageAroundKey(
            conversationId = conversationId,
            key = anchorMessageId,
            limit = limit
        )
    }
}
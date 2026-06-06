package com.nhuhuy.replee.feature_chat.domain.usecase.paging

import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import jakarta.inject.Inject

class GetMessageBeforeKeyUseCase @Inject constructor(
    private val paginatorRepository: PaginatorRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        anchorMessageId: String,
        pageSize: Int,
    ): NetworkResult<List<Message>> {
        return paginatorRepository.fetchMessageBeforeKey(
            conversationId = conversationId,
            key = anchorMessageId,
            limit = pageSize.toLong()
        )
    }
}
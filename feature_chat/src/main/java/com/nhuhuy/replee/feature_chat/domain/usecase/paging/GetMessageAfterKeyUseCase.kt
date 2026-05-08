package com.nhuhuy.replee.feature_chat.domain.usecase.paging

import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import javax.inject.Inject

class GetMessageAfterKeyUseCase @Inject constructor(
    private val paginatorRepository: PaginatorRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        pageSize: Int,
        anchorMessageId: String
    ): NetworkResult<List<Message>> {
        return paginatorRepository.fetchMessageAfterKey(
            conversationId = conversationId,
            key = anchorMessageId,
            limit = pageSize.toLong()
        )
    }
}
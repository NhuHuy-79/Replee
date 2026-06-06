package com.nhuhuy.replee.feature_chat.domain.usecase.paging

import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import javax.inject.Inject

class GetCurrentKeyUseCase @Inject constructor(
    private val paginatorRepository: PaginatorRepository
) {
    suspend operator fun invoke(conversationId: String): String? {
        return paginatorRepository.getCurrentKey(conversationId = conversationId)
    }
}
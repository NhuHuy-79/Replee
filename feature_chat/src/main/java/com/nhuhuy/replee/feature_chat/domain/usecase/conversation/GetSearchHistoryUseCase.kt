package com.nhuhuy.replee.feature_chat.domain.usecase.conversation

import com.nhuhuy.core.domain.model.SearchHistoryResult
import com.nhuhuy.core.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchHistoryUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(ownerId: String): Flow<List<SearchHistoryResult>> {
        return accountRepository.getSearchHistory(ownerId)
    }
}
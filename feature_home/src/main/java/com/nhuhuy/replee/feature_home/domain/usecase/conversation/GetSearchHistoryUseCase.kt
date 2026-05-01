package com.nhuhuy.replee.feature_home.domain.usecase.conversation

import com.nhuhuy.replee.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.model.chat.SearchHistoryResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchHistoryUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(ownerId: String): Flow<List<SearchHistoryResult>> {
        return accountRepository.getSearchHistory(ownerId)
    }
}

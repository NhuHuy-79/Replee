package com.nhuhuy.core.domain.usecase

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.SearchHistoryResult
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.repository.AccountRepository

class SearchAccountByEmailUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        ownerId: String,
        email: String
    ): NetworkResult<List<Account>> {
        return accountRepository.searchAccountsByEmail(email = email)
            .onSuccess { accounts ->
                val historyResults = accounts.map { account ->
                    SearchHistoryResult(
                        imgUrl = account.imageUrl,
                        uid = account.id,
                        name = account.name
                    )
                }
                accountRepository.updateHistory(
                    ownerId = ownerId,
                    list = historyResults
                )
            }
    }
}
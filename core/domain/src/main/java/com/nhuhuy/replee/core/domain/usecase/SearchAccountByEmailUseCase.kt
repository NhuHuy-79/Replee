package com.nhuhuy.replee.core.domain.usecase

import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.chat.SearchHistoryResult
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.core.domain.repository.AccountRepository

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

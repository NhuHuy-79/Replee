package com.nhuhuy.core.domain.usecase

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository

class SearchAccountByEmailUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        email: String
    ): NetworkResult<List<Account>> {
        return accountRepository.searchAccountsByEmail(email = email)
    }
}
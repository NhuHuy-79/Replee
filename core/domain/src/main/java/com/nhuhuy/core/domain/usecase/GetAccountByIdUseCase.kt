package com.nhuhuy.core.domain.usecase

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.repository.AccountRepository

class GetAccountByIdUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(uid: String): Account {
        return accountRepository.getAccountById(uid)
    }
}


package com.nhuhuy.replee.core.domain.usecase

import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.domain.repository.AccountRepository

class GetAccountByIdUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(uid: String): Account {
        return accountRepository.getAccountById(uid)
    }
}


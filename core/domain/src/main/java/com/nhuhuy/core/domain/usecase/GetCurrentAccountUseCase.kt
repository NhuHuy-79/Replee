package com.nhuhuy.core.domain.usecase

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.repository.AccountRepository

class GetCurrentAccountUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Account {
        return accountRepository.getCurrentAccount()
    }
}
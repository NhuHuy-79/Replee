package com.nhuhuy.replee.core.domain.usecase

import com.nhuhuy.replee.core.model.Account
import com.nhuhuy.replee.core.domain.repository.AccountRepository

class GetCurrentAccountUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Account {
        return accountRepository.getCurrentAccount()
    }
}

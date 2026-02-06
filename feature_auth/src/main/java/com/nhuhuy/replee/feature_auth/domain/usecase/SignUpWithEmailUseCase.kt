package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ): NetworkResult<Account> {
        return authRepository.signUpWithEmail(
            name = name,
            email = email,
            password = password
        ).onSuccess { account ->
            accountRepository.createAccount(account)
        }
    }
}
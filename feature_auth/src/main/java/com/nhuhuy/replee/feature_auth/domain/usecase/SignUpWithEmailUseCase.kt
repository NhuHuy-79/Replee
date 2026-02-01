package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.data.repository.IAccountRepository
import com.nhuhuy.replee.core.common.error_handling.NetworkResult
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: IAccountRepository
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
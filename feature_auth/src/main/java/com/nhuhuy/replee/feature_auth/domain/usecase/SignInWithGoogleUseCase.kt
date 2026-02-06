package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.feature_auth.data.GoogleIdTokenProvider
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val googleIdTokenProvider: GoogleIdTokenProvider,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): NetworkResult<String> {
        return googleIdTokenProvider.getIdToken()
            .onSuccess { token ->
                authRepository.signInWithGoogle(token)
                    .onSuccess { account ->
                        accountRepository.createAccount(account)
                    }
            }

    }
}
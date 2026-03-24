package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.data.utils.then
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
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
        ).then { account ->
            sessionManager.getCurrentDeviceToken()
                .then { token ->
                    accountRepository.createAccount(
                        token = token,
                        account = account
                    )
                }
        }.then {
            authRepository.provideAuthenticateToken().onSuccess { token ->
                sessionManager.refreshAuthenticationToken(token)
            }
        }.onFailure {
            sessionManager.logout()
        }
    }
}
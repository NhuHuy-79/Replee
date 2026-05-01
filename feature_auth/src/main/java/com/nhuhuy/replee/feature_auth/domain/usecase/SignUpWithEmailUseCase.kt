package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.model.Account
import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.model.onFailure
import com.nhuhuy.replee.core.model.onSuccess
import com.nhuhuy.replee.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.data.utils.then
import com.nhuhuy.replee.core.domain.repository.AuthRepository
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

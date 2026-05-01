package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.replee.core.data.utils.then
import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.error_handling.onFailure
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(idToken: String): NetworkResult<Account> {
        return authRepository.signInWithGoogle(idToken)
            .then { account ->
                sessionManager.getCurrentDeviceToken()
                    .then { token ->
                        accountRepository.createAccount(
                            account = account,
                            token = token
                        )
                    }
            }
            .then {
                authRepository.provideAuthenticateToken().onSuccess { token ->
                    sessionManager.refreshAuthenticationToken(token)
                }
            }
            .onFailure {
                sessionManager.logout()
            }
    }
}

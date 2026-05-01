package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.replee.core.data.utils.then
import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.error_handling.onFailure
import com.nhuhuy.replee.core.model.error_handling.onSuccess
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject


class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<String> {
        return authRepository.loginWithEmail(
            email = email,
            password = password
        ).then {
            sessionManager.getCurrentDeviceToken()
                .then { token ->
                    accountRepository.updateDeviceToken(token)
                }
        }.then {
            authRepository.provideAuthenticateToken().onSuccess { token ->
                sessionManager.refreshAuthenticationToken(token)
            }
        }.onFailure {
            //Rollback user if all request return a Failure.
            sessionManager.logOut()
        }
    }


}

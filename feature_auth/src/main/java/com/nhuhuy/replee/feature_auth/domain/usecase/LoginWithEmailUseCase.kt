package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.common.data.repository.PushNotificationRepository
import com.nhuhuy.replee.core.common.utils.then
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val pushNotificationRepository: PushNotificationRepository,
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<String> {
        return authRepository.loginWithEmail(
            email = email,
            password = password
        ).then {
            pushNotificationRepository.getCurrentToken()
                .then { token ->
                    accountRepository.updateDeviceToken(token)
                }
        }.then {
            authRepository.provideAuthenticateToken().onSuccess { token ->
                sessionManager.refreshAuthenticationToken(token)
            }
        }.onFailure {
            //Rollback user if all request return a Failure.
            sessionManager.logout()
        }
    }
}
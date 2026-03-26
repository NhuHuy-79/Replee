package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject


class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<String> {

        val loginResult = authRepository.loginWithEmail(email = email, password = password)

        when (loginResult) {
            is NetworkResult.Failure -> {
                sessionManager.logout()
                return loginResult
            }

            is NetworkResult.Success -> {
                val getDeviceToken = sessionManager.getCurrentDeviceToken()

                when (getDeviceToken) {
                    is NetworkResult.Failure -> {
                        sessionManager.logout()
                        return getDeviceToken
                    }

                    is NetworkResult.Success -> {
                        val deviceToken = getDeviceToken.data
                        accountRepository.updateDeviceToken(deviceToken)

                        val provideAuthToken = authRepository.provideAuthenticateToken()

                        when (provideAuthToken) {
                            is NetworkResult.Failure -> {
                                sessionManager.logout()
                                return provideAuthToken
                            }

                            is NetworkResult.Success -> {
                                val authToken = provideAuthToken.data
                                Timber.d("LoginWithEmailUseCase: Đang lưu token mới: $authToken")
                                sessionManager.refreshAuthenticationToken(authToken)
                                return NetworkResult.Success(loginResult.data)
                            }
                        }
                    }
                }

            }
        }
    }

    /* return authRepository.loginWithEmail(
         email = email,
         password = password
     ).then {
         sessionManager.getCurrentDeviceToken()
             .then { token ->
                 accountRepository.updateDeviceToken(token)
             }
     }.then {
         authRepository.provideAuthenticateToken().onSuccess { token ->
             val debug = token
             Timber.d("LoginWithEmailUseCase: Đang lưu token mới: $token")
             sessionManager.refreshAuthenticationToken(token)
         }
     }.onFailure {
         //Rollback user if all request return a Failure.
         sessionManager.logout()
     }
 }*/
}
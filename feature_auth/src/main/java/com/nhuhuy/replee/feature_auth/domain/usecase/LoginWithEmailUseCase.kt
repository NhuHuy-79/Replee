package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.common.data.repository.PushNotificationRepository
import com.nhuhuy.replee.core.common.utils.andThen
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val pushNotificationRepository: PushNotificationRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<String> {
        return authRepository.loginWithEmail(
            email = email,
            password = password
        ).andThen {
            pushNotificationRepository.getCurrentToken()
                .andThen { token ->
                    accountRepository.updateDeviceToken(token)
                }
        }
    }
}
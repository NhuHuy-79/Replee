package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.common.data.repository.PushNotificationRepository
import com.nhuhuy.replee.core.common.utils.flatMap
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val pushNotificationRepository: PushNotificationRepository
) {
    suspend operator fun invoke(idToken: String): NetworkResult<Account> {
        return authRepository.signInWithGoogle(idToken)
            .flatMap { account ->
                pushNotificationRepository.getCurrentToken()
                    .flatMap { token ->
                        accountRepository.createAccount(
                            account = account,
                            token = token
                        )
                    }
            }

    }
}
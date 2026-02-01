package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.replee.core.common.error_handling.NetworkResult
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class SendRecoveryEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): NetworkResult<Unit> {
        return authRepository.sendRecoverPasswordEmail(email)
    }
}
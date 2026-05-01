package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.domain.repository.AuthRepository
import javax.inject.Inject

class SendRecoveryEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): NetworkResult<Unit> {
        return authRepository.sendRecoverPasswordEmail(email)
    }
}

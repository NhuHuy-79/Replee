package com.nhuhuy.replee.feature_auth.domain.usecase

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<String> {
        return authRepository.loginWithEmail(
            email = email,
            password = password
        )
    }
}
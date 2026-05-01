package com.nhuhuy.replee.feature_auth.domain.repository

import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.error_handling.NetworkResult

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): NetworkResult<Account>
    suspend fun loginWithEmail(email: String, password: String): NetworkResult<String>
    suspend fun signUpWithEmail(
        name: String, email: String, password: String
    ): NetworkResult<Account>

    suspend fun sendRecoverPasswordEmail(email: String): NetworkResult<Unit>

    suspend fun provideAuthenticateToken(): NetworkResult<String>
}
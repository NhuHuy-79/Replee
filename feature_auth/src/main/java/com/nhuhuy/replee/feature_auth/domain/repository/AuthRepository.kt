package com.nhuhuy.replee.feature_auth.domain.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): NetworkResult<Account>
    suspend fun loginWithEmail(email: String, password: String): NetworkResult<String>
    suspend fun signUpWithEmail(
        name: String, email: String, password: String
    ): NetworkResult<Account>

    suspend fun sendRecoverPasswordEmail(email: String): NetworkResult<Unit>

    fun isUserLogged(): Boolean

    fun observeAuthState(): Flow<String?>
}
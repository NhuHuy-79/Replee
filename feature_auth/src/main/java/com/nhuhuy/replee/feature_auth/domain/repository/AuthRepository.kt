package com.nhuhuy.replee.feature_auth.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String) : Resource<String, RemoteFailure>
    suspend fun signUpWithEmail(name: String, email: String, password: String) : Resource<String, RemoteFailure>
    suspend fun sendRecoverPasswordEmail(email: String) : Resource<Unit, RemoteFailure>
    suspend fun provideCurrentUser(): Resource<String, RemoteFailure>
}
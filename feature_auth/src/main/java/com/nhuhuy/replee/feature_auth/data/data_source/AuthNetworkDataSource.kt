package com.nhuhuy.replee.feature_auth.data.data_source

import com.nhuhuy.replee.core.network.model.AccountDTO

interface AuthNetworkDataSource {
    suspend fun getCurrentAuthToken(): String
    suspend fun getCurrentUid(): String
    suspend fun signInWithEmail(email: String, password: String): String
    suspend fun signInWithGoogle(idToken: String): AccountDTO
    suspend fun signUpWithEmail(name: String, email: String, password: String): AccountDTO
    suspend fun sendRecoverPasswordEmail(email: String)
}
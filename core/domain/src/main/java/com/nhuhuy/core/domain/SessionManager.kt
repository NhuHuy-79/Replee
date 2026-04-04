package com.nhuhuy.core.domain

import com.nhuhuy.core.domain.model.AuthenticatedState
import com.nhuhuy.core.domain.model.NetworkResult
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    val authenticatedState: Flow<AuthenticatedState>
    val userIdState: Flow<String?>
    fun requireUserId(): String
    suspend fun refreshAuthenticationToken(token: String)
    fun getAuthenticationToken(): Flow<String>
    fun getUserIdOrNull(): String?
    suspend fun getNewAuthenticatedToken(): String
    suspend fun getCurrentDeviceToken(): NetworkResult<String>
    suspend fun logout()
}


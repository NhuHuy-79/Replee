package com.nhuhuy.replee.core.domain

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.validate.AuthenticatedState
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
}


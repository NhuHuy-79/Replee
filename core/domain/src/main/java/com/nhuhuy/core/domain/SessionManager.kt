package com.nhuhuy.core.domain

import com.nhuhuy.core.domain.model.AuthenticatedState
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    val authenticatedState: Flow<AuthenticatedState>
    fun requireUserId(): String
    suspend fun refreshAuthenticationToken(token: String)
    suspend fun getAuthenticationToken(): String?
    fun getUserIdOrNull(): String?
    suspend fun logout()
}


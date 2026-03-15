package com.nhuhuy.core.domain

import com.nhuhuy.core.domain.model.AuthenticatedState
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    val authState: Flow<String?>

    val authenticatedState: Flow<AuthenticatedState>
    fun requireUserId(): String
    fun getUserIdOrNull(): String?
    fun logout()
}


package com.nhuhuy.replee.usecase

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.AuthenticatedState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckAuthenticatedUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    operator fun invoke(): Flow<AuthenticatedState> = sessionManager.authenticatedState
}
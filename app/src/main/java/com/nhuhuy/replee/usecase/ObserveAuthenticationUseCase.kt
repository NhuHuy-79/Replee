package com.nhuhuy.replee.usecase

import com.nhuhuy.core.domain.SessionManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthenticationUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    operator fun invoke(): Flow<String?> = sessionManager.userIdState
}
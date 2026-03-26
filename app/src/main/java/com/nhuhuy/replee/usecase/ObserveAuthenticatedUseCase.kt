package com.nhuhuy.replee.usecase

import com.nhuhuy.core.domain.SessionManager
import javax.inject.Inject

class CheckAuthenticatedUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    operator fun invoke(): String? {
        return sessionManager.getUserIdOrNull()
    }
}
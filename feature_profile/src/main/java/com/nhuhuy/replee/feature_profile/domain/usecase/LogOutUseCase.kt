package com.nhuhuy.replee.feature_profile.domain.usecase

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.repository.PresenceRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val presenceRepository: PresenceRepository
) {
    suspend operator fun invoke() {
        val uid = sessionManager.getUserIdOrNull()
        uid?.let {
            presenceRepository.setOffline(uid)
        }
        sessionManager.logout()
    }
}
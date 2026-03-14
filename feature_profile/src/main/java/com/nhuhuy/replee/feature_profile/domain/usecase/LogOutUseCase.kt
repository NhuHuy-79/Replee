package com.nhuhuy.replee.feature_profile.domain.usecase

import com.nhuhuy.core.domain.repository.PresenceRepository
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val presenceRepository: PresenceRepository
) {
    suspend operator fun invoke() {
        val uid = profileRepository.logOut()
        presenceRepository.setOffline(uid)
    }
}
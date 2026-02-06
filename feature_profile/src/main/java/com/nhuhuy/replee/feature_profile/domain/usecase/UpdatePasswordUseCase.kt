package com.nhuhuy.replee.feature_profile.domain.usecase

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        oldPassword: String,
        newPassword: String,
    ): NetworkResult<Unit> = profileRepository.updatePassword(
        oldPassword = oldPassword,
        newPassword = newPassword
    )
}
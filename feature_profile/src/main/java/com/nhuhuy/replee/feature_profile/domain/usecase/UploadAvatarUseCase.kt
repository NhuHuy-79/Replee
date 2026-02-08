package com.nhuhuy.replee.feature_profile.domain.usecase

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(byteArray: ByteArray): NetworkResult<String> {
        return profileRepository.updateUserImage(byteArray)
    }
}
package com.nhuhuy.replee.feature_profile.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult

interface ProfileRepository {
    suspend fun updateUserImage(byteArray: ByteArray): NetworkResult<String>

    suspend fun logOut()

    suspend fun updatePassword(oldPassword: String, newPassword: String): NetworkResult<Unit>
}
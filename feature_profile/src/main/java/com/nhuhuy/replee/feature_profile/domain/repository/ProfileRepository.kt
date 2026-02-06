package com.nhuhuy.replee.feature_profile.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource

interface ProfileRepository {

    suspend fun updateNewPassword(
        old: String,
        new: String
    ) : Resource<Unit, RemoteFailure>
    suspend fun logOut()

    suspend fun updatePassword(oldPassword: String, newPassword: String): NetworkResult<Unit>
}
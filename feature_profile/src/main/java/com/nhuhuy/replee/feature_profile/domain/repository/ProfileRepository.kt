package com.nhuhuy.replee.feature_profile.domain.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.Failure
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource

interface ProfileRepository {

    suspend fun updatePassword(
        old: String,
        new: String
    ) : Resource<Unit, RemoteFailure>
    fun logOut()
}
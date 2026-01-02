package com.nhuhuy.replee.feature_profile.domain.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource

interface ProfileRepository {
    suspend fun getUserInformation() : Resource<Account, RemoteFailure>

}
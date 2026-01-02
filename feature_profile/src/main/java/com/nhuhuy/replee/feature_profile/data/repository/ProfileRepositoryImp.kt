package com.nhuhuy.replee.feature_profile.data.repository

import com.nhuhuy.replee.core.common.data.AccountDataSource
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val accountDataSource: AccountDataSource,
) : ProfileRepository{
    override suspend fun getUserInformation(): Resource<Account, RemoteFailure> {
        TODO("Not yet implemented")
    }

}
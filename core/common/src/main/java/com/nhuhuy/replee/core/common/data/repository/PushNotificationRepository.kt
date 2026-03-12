package com.nhuhuy.replee.core.common.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.network.data_source.TokenNetworkDataSource
import javax.inject.Inject

interface PushNotificationRepository {
    suspend fun updateToken(newToken: String): NetworkResult<String>
    suspend fun getCurrentToken(): NetworkResult<String>
    suspend fun pushNotification(): NetworkResult<Unit>
}

class PushNotificationRepositoryImp @Inject constructor(
    private val tokenNetworkDataSource: TokenNetworkDataSource
) : PushNotificationRepository {
    override suspend fun updateToken(newToken: String): NetworkResult<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentToken(): NetworkResult<String> = execute {
        tokenNetworkDataSource.getDeviceToken()
    }

    override suspend fun pushNotification(): NetworkResult<Unit> {
        TODO("Not yet implemented")
    }

}
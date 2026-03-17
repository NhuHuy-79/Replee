package com.nhuhuy.replee.core.common.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.network.api.fcm.ConversationNotificationRequest
import com.nhuhuy.replee.core.network.data_source.PushNotificationNetworkDataSource
import javax.inject.Inject

interface PushNotificationRepository {
    suspend fun getCurrentToken(): NetworkResult<String>
    suspend fun pushNotification(
        deviceToken: String,
        authenticationId: String,
        request: ConversationNotificationRequest
    ): NetworkResult<Unit>
}

class PushNotificationRepositoryImp @Inject constructor(
    private val pushNotificationNetworkDataSource: PushNotificationNetworkDataSource,
) : PushNotificationRepository {

    override suspend fun getCurrentToken(): NetworkResult<String> = execute {
        pushNotificationNetworkDataSource.getDeviceToken()
    }

    override suspend fun pushNotification(
        deviceToken: String,
        authenticationId: String,
        request: ConversationNotificationRequest
    ): NetworkResult<Unit> {
        return execute {
            pushNotificationNetworkDataSource.sendNotification(
                deviceToken = deviceToken,
                authenticationId = authenticationId,
                request = request
            )
        }
    }
}
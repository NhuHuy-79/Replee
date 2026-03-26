package com.nhuhuy.replee.core.network.data_source

import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.replee.core.network.api.fcm.ConversationNotificationRequest
import com.nhuhuy.replee.core.network.api.fcm.FcmApi
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface PushNotificationNetworkDataSource {
    suspend fun getDeviceToken(): String
    suspend fun sendNotification(
        deviceToken: String,
        authenticationId: String,
        request: ConversationNotificationRequest
    )
}

class PushNotificationNetworkDataSourceImp @Inject constructor(
    private val fcmApi: FcmApi,
    private val firebaseMessaging: FirebaseMessaging
) : PushNotificationNetworkDataSource {
    override suspend fun getDeviceToken(): String {
        return firebaseMessaging.token.await()
    }

    override suspend fun sendNotification(
        deviceToken: String,
        authenticationId: String,
        request: ConversationNotificationRequest
    ) {
        fcmApi.sendNotification(
            bearerToken = "Bearer $authenticationId",
            deviceToken = deviceToken,
            request = request
        )
    }
}
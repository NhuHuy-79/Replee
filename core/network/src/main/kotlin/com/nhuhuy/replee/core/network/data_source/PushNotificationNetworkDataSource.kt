package com.nhuhuy.replee.core.network.data_source

import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.replee.core.network.api.KtorService
import com.nhuhuy.replee.core.network.api.model.ConversationNotificationRequest
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
    private val ktorService: KtorService,
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
        ktorService.sendConversationMessage(
            authenticationId = authenticationId,
            deviceToken = deviceToken,
            request = request
        )
    }

}
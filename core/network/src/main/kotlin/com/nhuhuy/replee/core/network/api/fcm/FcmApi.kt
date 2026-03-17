package com.nhuhuy.replee.core.network.api.fcm

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


const val FCM_URL = "https://192.168.1.5:3000/api/v1/notifications"

interface FcmApi {
    @POST("/send")
    suspend fun sendNotification(
        @Header("Authorization") bearerToken: String,
        @Header("device-token") deviceToken: String,
        @Body request: ConversationNotificationRequest
    ): Response<ConversationNotificationRequest>
}
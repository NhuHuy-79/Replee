package com.nhuhuy.replee.core.network.api.fcm

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


const val REPLEE_BACKEND_URL = "https://replee-deploy.vercel.app/"

interface FcmApi {
    @POST("api/v1/notifications/send")
    suspend fun sendNotification(
        @Header("Authorization") bearerToken: String,
        @Header("device-token") deviceToken: String,
        @Body request: ConversationNotificationRequest
    ): Response<NotificationResponse>
}

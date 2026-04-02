package com.nhuhuy.replee.notification

import com.google.firebase.messaging.RemoteMessage
import com.nhuhuy.replee.core.network.api.fcm.ContentType
import com.nhuhuy.replee.core.network.api.fcm.NotificationResponse
import timber.log.Timber
import javax.inject.Inject

class NotificationParser @Inject constructor() {
    fun getNotificationBody(remoteMessage: RemoteMessage): NotificationResponse? {
        val data = remoteMessage.data

        if (data.isEmpty()) return null

        return try {
            val type = data["type"] ?: "PLAIN_TEXT"
            NotificationResponse(
                senderId = data["senderId"] ?: "",
                receiverId = data["receiverId"] ?: "",
                conversationId = data["conversationId"] ?: "",
                senderName = data["senderName"] ?: "Unknown",
                senderImg = data["senderImg"] ?: "",
                content = data["content"] ?: "",
                type = ContentType.valueOf(type),
                messageId = data["messageId"] ?: ""
            )
        } catch (e: Exception) {
            Timber.e("Lỗi map dữ liệu: ${e.message}")
            null
        }
    }
}
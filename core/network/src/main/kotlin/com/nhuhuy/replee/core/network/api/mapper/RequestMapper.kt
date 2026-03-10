package com.nhuhuy.replee.core.network.api.mapper

import com.nhuhuy.replee.core.network.api.model.ConversationNotificationRequest
import com.nhuhuy.replee.core.network.api.model.NotificationResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RequestMapper @Inject constructor(
    private val json: Json,
){
    fun parseToString(body: ConversationNotificationRequest): String {
        return json.encodeToString(ConversationNotificationRequest.serializer(), body)
    }

    fun parseToNetworkMessage(data: String): NotificationResponse {
        return json.decodeFromString<NotificationResponse>(data)
    }

}

//Sender -> JSON -> BE -> CLIENT -> HANDLER -> NOTIFICATION_DATA -> SHOW NOTIFICATION
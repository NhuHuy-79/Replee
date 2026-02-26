package com.nhuhuy.replee.core.network.network.mapper

import com.nhuhuy.replee.core.network.network.model.ConversationNotificationRequest
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RequestMapper @Inject constructor(
    private val json: Json,
){
    fun parseToString(body: ConversationNotificationRequest): String {
        return json.encodeToString(ConversationNotificationRequest.serializer(), body)
    }

    fun parseToNetworkMessage(data: String): ConversationNotificationRequest {
        return json.decodeFromString<ConversationNotificationRequest>(data)
    }

}

//Sender -> JSON -> BE -> CLIENT -> HANDLER -> NOTIFICATION_DATA -> SHOW NOTIFICATION
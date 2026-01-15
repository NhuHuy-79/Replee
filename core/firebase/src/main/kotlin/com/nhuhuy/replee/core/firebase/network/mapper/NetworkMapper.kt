package com.nhuhuy.replee.core.firebase.network.mapper

import com.nhuhuy.replee.core.firebase.network.model.ConversationMessage
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NetworkMapper @Inject constructor(
    private val json: Json,
){
    fun parseToString(body: ConversationMessage) : String {
        return json.encodeToString(ConversationMessage.serializer(), body)
    }

    fun parseToNetworkMessage(data : String) : ConversationMessage {
        return json.decodeFromString<ConversationMessage>(data)
    }

}

//Sender -> JSON -> BE -> CLIENT -> HANDLER -> NOTIFICATION_DATA -> SHOW NOTIFICATION
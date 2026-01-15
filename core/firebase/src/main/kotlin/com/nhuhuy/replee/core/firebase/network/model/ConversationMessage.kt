package com.nhuhuy.replee.core.firebase.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("conversation")
data class ConversationMessage(
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val content: String,
    val conversationId: String,
) : NetworkMessage

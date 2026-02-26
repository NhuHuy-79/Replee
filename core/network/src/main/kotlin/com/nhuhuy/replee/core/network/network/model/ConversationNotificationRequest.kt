package com.nhuhuy.replee.core.network.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("conversation")
data class ConversationNotificationRequest(
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val content: String,
    val imgUrl: String,
    val conversationId: String,
) : FCMRequest

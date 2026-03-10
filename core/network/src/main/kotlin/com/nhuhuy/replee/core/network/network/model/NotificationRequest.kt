package com.nhuhuy.replee.core.network.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("conversation")
data class ConversationNotificationRequest(
    val senderName: String,
    val receiverId: String,
    val content: String,
    val contentType: ContentType,
    val imgUrl: String,
    val conversationId: String,
) : FCMRequest

enum class ContentType {
    PLAIN_TEXT,
    IMAGE_URL,
    VIDEO_URL,

}
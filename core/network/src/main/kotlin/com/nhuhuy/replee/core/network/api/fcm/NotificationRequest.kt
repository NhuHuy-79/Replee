package com.nhuhuy.replee.core.network.api.fcm

import kotlinx.serialization.Serializable

@Serializable
data class ConversationNotificationRequest(
    val messageId: String,
    val senderName: String,
    val receiverId: String,
    val content: String,
    val contentType: ContentType,
    val imgUrl: String,
    val conversationId: String,
) : FcmRequest


enum class ContentType {
    PLAIN_TEXT,
    IMAGE_URL,
    VIDEO_URL,
}
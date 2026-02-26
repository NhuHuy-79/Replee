package com.nhuhuy.replee.notification

data class ConversationNotificationResponse(
    val imgUrl: String? = null,
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val senderName: String,
    val message: String,
)

package com.nhuhuy.replee.notification

data class NotificationBody(
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val senderName: String,
    val message: String,
)

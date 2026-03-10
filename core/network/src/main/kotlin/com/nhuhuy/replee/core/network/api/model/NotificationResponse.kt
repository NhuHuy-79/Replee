package com.nhuhuy.replee.core.network.api.model

data class NotificationResponse(
    val imgUrl: String? = null,
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val senderName: String,
    val message: String,
)
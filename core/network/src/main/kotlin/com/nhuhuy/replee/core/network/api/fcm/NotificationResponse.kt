package com.nhuhuy.replee.core.network.api.fcm

import kotlinx.serialization.SerialName


data class NotificationResponse(
    @SerialName("imgUrl")
    val imgUrl: String? = null,
    @SerialName("conversationId")
    val conversationId: String,
    @SerialName("senderId")
    val senderId: String,
    @SerialName("receiverId")
    val receiverId: String,
    @SerialName("senderName")
    val senderName: String,
    @SerialName("message")
    val message: String,
)
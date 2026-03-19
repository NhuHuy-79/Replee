package com.nhuhuy.replee.core.network.api.fcm

import com.google.gson.annotations.SerializedName


data class NotificationResponse(
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("receiverId")
    val receiverId: String,
    @SerializedName("conversationId")
    val conversationId: String,
    @SerializedName("senderImg")
    val senderImg: String = "",
    @SerializedName("senderName")
    val senderName: String = "",
    @SerializedName("content")
    val content: String = "",
    @SerializedName("type")
    val type: ContentType
)
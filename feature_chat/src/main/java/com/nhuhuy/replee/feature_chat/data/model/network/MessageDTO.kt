package com.nhuhuy.replee.feature_chat.data.model.network

data class MessageDTO(
    val conversationId: String = "",
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val sendAt: Long?= null,
    val seen: Boolean = false
)
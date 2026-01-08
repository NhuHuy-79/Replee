package com.nhuhuy.replee.feature_chat.domain.model

data class Message(
    val conversationId: String,
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val seen: Boolean,
    val sentAt: Long? = null
)

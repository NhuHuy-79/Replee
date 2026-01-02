package com.nhuhuy.replee.feature_chat.domain.model

data class Conversation(
    val id: String,
    val members: List<ConversationOtherUser>,
    val createdAt: Long? = null,
    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    val lastMessageTime: Long? = null,
)

data class ConversationOtherUser(
    val uid: String = "404",
    val name: String = "No one",
)


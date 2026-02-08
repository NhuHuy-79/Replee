package com.nhuhuy.replee.feature_chat.domain.model

data class Conversation(
    val id: String = "",
    val owner: ConversationOtherUser = ConversationOtherUser(),
    val otherUser: ConversationOtherUser = ConversationOtherUser(),
    val unreadMessageCount: Int = 0,
    val createdAt: Long? = null,
    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    val lastMessageTime: Long? = null,
    //Add field
    val seedColor: Long = 0xFF1C6586,
    val muted: Boolean = false,
    val pinned: Boolean = false,
    val blocked: Boolean = false,
    val deleted: Boolean = false
)

data class ConversationOtherUser(
    val imgUrl: String = "",
    val nick: String = "",
    val uid: String = "404",
    val name: String = "No one",
)





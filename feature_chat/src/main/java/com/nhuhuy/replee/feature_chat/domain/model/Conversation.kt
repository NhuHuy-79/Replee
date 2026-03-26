package com.nhuhuy.replee.feature_chat.domain.model

data class Conversation(
    val id: String = "",
    val otherUserId: String = "",
    val ownerUserId: String = "",
    val ownerNickName: String = "",
    val otherUserNickName: String = "",
    val otherUserName: String = "",
    val otherUserImg: String = "",
    val createdAt: Long? = null,
    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    val lastMessageTime: Long? = null,
    val lastMessageType: MessageType = MessageType.TEXT,
    //Add field
    val unreadMessageCount: Int = 0,
    val otherUserOnline: Boolean = false,
    val muted: Boolean = false,
    val pinned: Boolean = false,
    val blocked: Boolean = false,
    val deleted: Boolean = false
)





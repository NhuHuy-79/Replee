package com.nhuhuy.replee.feature_chat.domain.model.converastion

import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType

data class Conversation(
    val id: String = "",
    val otherUserId: String = "",
    val ownerUserId: String = "",
    val ownerNickName: String = "",
    val otherUserNickName: String = "",
    val otherUserName: String = "",
    val otherUserImg: String = "",
    val createdAt: Long? = null,
    val lastMessageId: String = "",
    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    val lastMessageTime: Long? = null,
    val lastMessageType: MessageType = MessageType.TEXT,
    val lastDeletedMessageId: String? = null,
    val lastReadBy: Long? = null,
    //Add field
    val unreadMessageCount: Int = 0,
    val otherUserOnline: Boolean = false,
    val muted: Boolean = false,
    val pinned: Boolean = false,
    val blocked: Boolean = false,
    val deleted: Boolean = false
)
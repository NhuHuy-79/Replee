package com.nhuhuy.replee.feature_chat.data.model.network

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType

data class MessageDTO(
    val conversationId: String = "",
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val deleted: Boolean = false,
    @ServerTimestamp
    val sendAt: Timestamp? = null,
    val seen: Boolean = false,
    val type: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SYNCED,
    val url: String? = null,
    val repliedMessageContent: String? = null,
    val repliedMessageId: String? = null,
    val repliedMessageSenderId: String? = null,
    val repliedMessageType: MessageType? = null,
    val repliedMessageRemoteUrl: String? = null,
)
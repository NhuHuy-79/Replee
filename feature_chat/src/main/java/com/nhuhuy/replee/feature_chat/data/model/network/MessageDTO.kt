package com.nhuhuy.replee.feature_chat.data.model.network

import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType

data class MessageDTO(
    val conversationId: String = "",
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val sendAt: Long?= null,
    val seen: Boolean = false,
    val type: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SYNCED
)
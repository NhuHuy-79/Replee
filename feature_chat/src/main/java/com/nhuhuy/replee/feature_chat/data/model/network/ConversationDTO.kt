package com.nhuhuy.replee.feature_chat.data.model.network

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.replee.feature_chat.domain.model.MessageType

data class ConversationDTO(
    val id: String = "",
    val unReadMessages: Map<String, Int> = emptyMap(),

    //Unchanged data
    val memberIds : List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,

    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    val lastMessageTime: Long? = null,
    val lastMessageType: MessageType = MessageType.TEXT,

    val nickName: Map<String, String> = emptyMap(),
    val isMuted: Map<String, Boolean> = emptyMap(),
    val isPinned: Map<String, Boolean> = emptyMap(),
    val isDeleted: Map<String, Boolean> = emptyMap(),
    val isBlocked: Map<String, Boolean> = emptyMap(),
)


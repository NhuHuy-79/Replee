package com.nhuhuy.replee.feature_chat.data.model.network

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.replee.feature_chat.domain.model.MessageType

@Keep
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

    @get:PropertyName("isMuted")
    val isMuted: Map<String, Boolean> = emptyMap(),

    @get:PropertyName("isPinned")
    val isPinned: Map<String, Boolean> = emptyMap(),

    @get:PropertyName("isDeleted")
    val isDeleted: Map<String, Boolean> = emptyMap(),

    @get:PropertyName("isBlocked")
    val isBlocked: Map<String, Boolean> = emptyMap(),
)


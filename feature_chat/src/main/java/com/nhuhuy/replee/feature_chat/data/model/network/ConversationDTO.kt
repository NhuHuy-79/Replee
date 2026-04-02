package com.nhuhuy.replee.feature_chat.data.model.network

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType

@Keep
data class ConversationDTO(
    val id: String = "",
    val unReadMessages: Map<String, Int> = emptyMap(),

    //Unchanged data
    val memberIds : List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,

    val lastMessageId: String = "",
    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    val lastMessageTime: Long? = null,
    val lastMessageType: MessageType = MessageType.TEXT,
    val lastDeletedMessageId: String? = null,

    val nickName: Map<String, String> = emptyMap(),

    @get:PropertyName("isMuted")
    val isMuted: Map<String, Boolean> = emptyMap(),

    @get:PropertyName("isPinned")
    val isPinned: Map<String, Boolean> = emptyMap(),

    @get:PropertyName("isDeleted")
    val isDeleted: Map<String, Boolean> = emptyMap(),

    @get:PropertyName("isBlocked")
    val isBlocked: Map<String, Boolean> = emptyMap(),

    val lastReadBy: Map<String, Long?> = emptyMap()
)


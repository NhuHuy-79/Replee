package com.nhuhuy.replee.feature_chat.data.model.local

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class MessageEntity(
    val conversationId: String = "",
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val sendAt: Long?= null,
    val seen: Boolean = false
)

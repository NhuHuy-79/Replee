package com.nhuhuy.replee.core.network.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.nhuhuy.replee.core.model.MessageStatus
import com.nhuhuy.replee.core.model.MessageType

@Keep
data class MessageDTO(
    val conversationId: String = "",
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    @ServerTimestamp
    val sendAt: Timestamp? = null,
    val pinned: Boolean = false,
    val type: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SYNCED,
    val url: String? = null,
    val repliedMessageContent: String? = null,
    val repliedMessageId: String? = null,
    val repliedMessageSenderId: String? = null,
    val repliedMessageType: MessageType? = null,
    val repliedMessageRemoteUrl: String? = null,
    //ver1
    val reactions: Map<String, List<String>> = emptyMap()
)

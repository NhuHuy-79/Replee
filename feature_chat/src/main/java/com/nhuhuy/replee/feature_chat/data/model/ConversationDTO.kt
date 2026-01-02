package com.nhuhuy.replee.feature_chat.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ConversationDTO(
    val id: String = "",
    val membersId: List<ConversationDTOUser> = emptyList(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    @ServerTimestamp
    val lastMessageTime: Timestamp? = null,
)

data class ConversationDTOUser(
    val uid: String = "404",
    val name: String = "No one",
)

package com.nhuhuy.replee.feature_chat.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ConversationDTO(
    val id: String = "",
    val user1: ConversationDTOUser = ConversationDTOUser(),
    val user2: ConversationDTOUser = ConversationDTOUser(),
    val unreadMessageCount: UnreadMessageCount = UnreadMessageCount(),
    val memberIds : List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    val lastMessageTime: Long? = null,
)

data class ConversationDTOUser(
    val uid: String = "404",
    val name: String = "No one",
)

data class UnreadMessageCount(
    val user1: Int = 0,
    val user2: Int = 0
)

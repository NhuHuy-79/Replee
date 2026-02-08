package com.nhuhuy.replee.feature_chat.data.model.network

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
    //Add field
    val mutedBy: List<String> = emptyList(),
    val pinnedBy: List<String> = emptyList(),
    val deletedBy: List<String> = emptyList(),
    val blockedBy: List<String> = emptyList(),
    val seedColor: Long = 0xFF1C6586
)

data class ConversationDTOUser(
    val nick: String = "",
    val uid: String = "404",
    val name: String = "No one",
    val imgUrl: String = "",
)

data class UnreadMessageCount(
    val user1: Int = 0,
    val user2: Int = 0
)

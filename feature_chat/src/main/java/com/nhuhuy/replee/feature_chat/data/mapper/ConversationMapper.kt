package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.firebase.utils.toMilliseconds
import com.nhuhuy.replee.feature_chat.data.model.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.model.ConversationDTOUser
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.ConversationOtherUser

fun ConversationDTOUser.toConversationOtherUser() : ConversationOtherUser {
    return ConversationOtherUser(
        uid = uid,
        name = name
    )
}

fun ConversationDTO.toConversation(
    ownerId: String
) : Conversation {
    val owner = if (user1.uid == ownerId) user1 else user2
    val otherUser = if (user1.uid == ownerId) user2 else user1
    val unreadMessageCount = if (user1.uid == ownerId) unreadMessageCount.user1 else unreadMessageCount.user2

    return Conversation(
        id = id,
        owner =  owner.toConversationOtherUser(),
        otherUser = otherUser.toConversationOtherUser(),
        createdAt = createdAt?.toMilliseconds(),
        lastSenderId = lastSenderId,
        lastMessageTime = lastMessageTime,
        lastMessageContent = lastMessageContent,
        unreadMessageCount = unreadMessageCount,
    )
}


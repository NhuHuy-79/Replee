package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.firebase.utils.toMilliseconds
import com.nhuhuy.replee.core.database.entity.conversation.ConversationAndUser
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTOUser
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.ConversationOtherUser

fun ConversationDTOUser.UserInConversation() : ConversationOtherUser {
    return ConversationOtherUser(
        uid = uid,
        name = name
    )
}


fun Conversation.toConversationEntity(): ConversationEntity {
    return ConversationEntity(
        id = this.id,
        ownerId = this.owner.uid,
        otherUserId = this.otherUser.uid,
        createdAt = this.createdAt,
        lastSenderId = this.lastSenderId,
        lastMessageTime = this.lastMessageTime,
        lastMessageContent = this.lastMessageContent,
        unreadMessageCount = this.unreadMessageCount
    )
}

fun ConversationAndUser.toConversationDTO() : ConversationDTO {
    val user1 = ConversationDTOUser(
        uid = owner?.uid.orEmpty(),
        name = owner?.name.orEmpty()
    )

    val user2 = ConversationDTOUser(
        uid = otherUser?.uid.orEmpty(),
        name = otherUser?.name.orEmpty()
    )
    return ConversationDTO(
        id = conversation.id,
        user1 = user1,
        user2 = user2,
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
        owner =  owner.UserInConversation(),
        otherUser = otherUser.UserInConversation(),
        createdAt = createdAt?.toMilliseconds(),
        lastSenderId = lastSenderId,
        lastMessageTime = lastMessageTime,
        lastMessageContent = lastMessageContent,
        unreadMessageCount = unreadMessageCount,
    )
}

fun ConversationAndUser.toConversation(): Conversation {

    val owner = ConversationOtherUser(
        uid = owner?.uid.orEmpty(),
        name = owner?.name.orEmpty()
    )
    val otherUser = ConversationOtherUser(
        uid = otherUser?.uid.orEmpty(),
        name = otherUser?.name.orEmpty()
    )

    return Conversation(
        id = conversation.id,
        owner = owner,
        otherUser = otherUser,
        unreadMessageCount = conversation.unreadMessageCount,
        createdAt = conversation.createdAt,
        lastMessageContent = conversation.lastMessageContent,
        lastSenderId = conversation.lastSenderId,
        lastMessageTime = conversation.lastMessageTime
    )
}


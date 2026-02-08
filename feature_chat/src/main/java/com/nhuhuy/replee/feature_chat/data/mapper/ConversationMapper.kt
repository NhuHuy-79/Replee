package com.nhuhuy.replee.feature_chat.data.mapper

import com.google.firebase.firestore.FieldValue
import com.nhuhuy.replee.core.database.entity.conversation.ConversationAndUser
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.core.firebase.utils.toMilliseconds
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTOUser
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationPatch
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.ConversationOtherUser

fun ConversationDTOUser.toUserInConversation() : ConversationOtherUser {
    return ConversationOtherUser(
        imgUrl = imgUrl,
        nick = nick,
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
        unreadMessageCount = this.unreadMessageCount,
        ownerNick = this.owner.nick,
        otherUserNick = this.otherUser.nick,
        muted = this.muted,
        ownerImg = this.owner.imgUrl,
        otherUserImg = this.otherUser.imgUrl,
        blocked = this.blocked,
        pinned = this.pinned,
        deleted = this.deleted,
        synced = false,
        lastTimeSyncs = System.currentTimeMillis()
    )
}

fun ConversationAndUser.toConversationDTO() : ConversationDTO {
    val user1 = ConversationDTOUser(
        nick = conversation.ownerNick,
        uid = owner?.uid.orEmpty(),
        name = owner?.name.orEmpty(),
        imgUrl = owner?.imageUrl.orEmpty()
    )

    val user2 = ConversationDTOUser(
        nick = conversation.otherUserNick,
        uid = otherUser?.uid.orEmpty(),
        name = otherUser?.name.orEmpty(),
        imgUrl = otherUser?.imageUrl.orEmpty()
    )
    return ConversationDTO(
        id = conversation.id,
        user1 = user1,
        user2 = user2,
        memberIds = listOf(user1.uid, user2.uid),
        lastSenderId = conversation.lastSenderId,
        lastMessageTime = conversation.lastMessageTime,
        lastMessageContent = conversation.lastMessageContent
    )
}

fun ConversationAndUser.toConversationPatch(): ConversationPatch {
    return ConversationPatch(
        id = this.conversation.id,
        mapFieldValue = this.toFieldValueMap(),
        mapLastMessage = this.toLastMessageMap()
    )
}

fun ConversationAndUser.toFieldValueMap(): Map<String, FieldValue> {
    val mutedList = unionOrRemoveArray(conversation.muted, conversation.ownerId)
    val pinnedList = unionOrRemoveArray(conversation.pinned, conversation.ownerId)
    val blockedList = unionOrRemoveArray(conversation.blocked, conversation.ownerId)
    val deletedList = unionOrRemoveArray(conversation.deleted, conversation.ownerId)
    return mapOf(
        "mutedBy" to mutedList,
        "pinnedBy" to pinnedList,
        "blockedBy" to blockedList,
        "deletedBy" to deletedList
    )
}

fun ConversationAndUser.toLastMessageMap(): Map<String, Any?> {
    return mapOf(
        "lastSenderId" to conversation.lastSenderId,
        "lastMessageTime" to conversation.lastMessageTime,
        "lastMessageContent" to conversation.lastMessageContent
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
        owner =  owner.toUserInConversation(),
        otherUser = otherUser.toUserInConversation(),
        createdAt = createdAt?.toMilliseconds(),
        lastSenderId = lastSenderId,
        lastMessageTime = lastMessageTime,
        lastMessageContent = lastMessageContent,
        unreadMessageCount = unreadMessageCount,
        seedColor = seedColor,
        muted = mutedBy.contains(otherUser.uid),
        pinned = pinnedBy.contains(otherUser.uid),
        blocked = blockedBy.contains(otherUser.uid),
        deleted = deletedBy.contains(otherUser.uid)
    )
}

fun ConversationAndUser.toConversation(): Conversation {

    val owner = ConversationOtherUser(
        imgUrl = conversation.ownerImg,
        nick = conversation.ownerNick,
        uid = owner?.uid.orEmpty(),
        name = owner?.name.orEmpty()
    )
    val otherUser = ConversationOtherUser(
        imgUrl = conversation.otherUserImg,
        nick = conversation.otherUserNick,
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
        lastMessageTime = conversation.lastMessageTime,
        muted = conversation.muted,
        pinned = conversation.pinned,
        blocked = conversation.blocked,
        deleted = conversation.deleted
    )
}


private fun unionOrRemoveArray(union: Boolean, element: String) : FieldValue {
    return if (union) FieldValue.arrayUnion(element) else FieldValue.arrayRemove(element)
}

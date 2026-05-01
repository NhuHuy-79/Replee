package com.nhuhuy.replee.core.data.mapper

import com.google.firebase.firestore.FieldValue
import com.nhuhuy.replee.core.database.entity.conversation.ConversationAndUser
import com.nhuhuy.replee.core.model.MessageType
import com.nhuhuy.replee.core.network.model.ConversationDTO

//Create new object to upload
fun ConversationAndUser.createConversationDTO(): ConversationDTO {
    return ConversationDTO(
        id = conversation.id,
        nickName = mapOf(
            conversation.ownerId to conversation.ownerNick,
            conversation.otherUserId to conversation.otherUserNick
        ),
        lastMessageId = conversation.lastMessageId,
        lastSenderId = conversation.lastSenderId,
        lastMessageTime = conversation.lastMessageTime,
        lastMessageContent = conversation.lastMessageContent,
        lastMessageType = MessageType.valueOf(conversation.lastMessageType),
        lastDeletedMessageId = conversation.lastDeletedMessageId,
        isBlocked = mapOf(conversation.ownerId to conversation.blocked),
        isPinned = mapOf(conversation.ownerId to conversation.pinned),
        isMuted = mapOf(conversation.ownerId to conversation.muted),
        isDeleted = mapOf(conversation.ownerId to conversation.deleted),
        lastTimeDeleted = conversation.lastTimeDeleted?.let { mapOf(conversation.ownerId to it) }
            ?: emptyMap(),
        memberIds = listOf(conversation.ownerId, conversation.otherUserId),
        unReadMessages = mapOf(conversation.ownerId to conversation.unreadMessageCount)
    )
}

fun ConversationAndUser.toUpdatePatch(uid: String): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    map["id"] = conversation.id
    map["isMuted.$uid"] = conversation.muted
    map["isPinned.$uid"] = conversation.pinned
    map["isBlocked.$uid"] = conversation.blocked
    map["isDeleted.$uid"] = conversation.deleted
    map["lastTimeDeleted.$uid"] = conversation.lastTimeDeleted ?: 0L
    map["nickName.$uid"] = conversation.otherUserNick
    map["unReadMessages.$uid"] = conversation.unreadMessageCount

    map["lastDeletedMessageId"] = conversation.lastDeletedMessageId.orEmpty()
    map["lastMessageId"] = conversation.lastMessageId
    map["lastSenderId"] = conversation.lastSenderId
    map["lastMessageTime"] = FieldValue.serverTimestamp()
    map["lastMessageContent"] = conversation.lastMessageContent
    map["lastMessageType"] = conversation.lastMessageType

    map["lastReadBy.$uid"] = conversation.lastReadBy ?: FieldValue.serverTimestamp()

    return map

}

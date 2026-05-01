package com.nhuhuy.replee.core.network.mapper

import com.google.firebase.firestore.FieldValue
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.core.network.model.ConversationDTO
import com.nhuhuy.replee.core.network.utils.toMilliseconds

//Create new object to upload
fun createConversationDTO(
    conversationId: String,
    ownerId: String,
    otherUserId: String,
    ownerNick: String,
    otherUserNick: String,
    lastMessageId: String,
    lastSenderId: String,
    lastMessageTime: Long?,
    lastMessageContent: String,
    lastMessageType: String,
    lastDeletedMessageId: String?,
    blocked: Boolean,
    pinned: Boolean,
    muted: Boolean,
    deleted: Boolean,
    lastTimeDeleted: Long?,
    unreadMessageCount: Int,
): ConversationDTO {
    return ConversationDTO(
        id = conversationId,
        nickName = mapOf(
            ownerId to ownerNick,
            otherUserId to otherUserNick
        ),
        lastMessageId = lastMessageId,
        lastSenderId = lastSenderId,
        lastMessageTime = lastMessageTime,
        lastMessageContent = lastMessageContent,
        lastMessageType = MessageType.valueOf(lastMessageType),
        lastDeletedMessageId = lastDeletedMessageId,
        isBlocked = mapOf(ownerId to blocked),
        isPinned = mapOf(ownerId to pinned),
        isMuted = mapOf(ownerId to muted),
        isDeleted = mapOf(ownerId to deleted),
        lastTimeDeleted = lastTimeDeleted?.let { mapOf(ownerId to it) }
            ?: emptyMap(),
        memberIds = listOf(ownerId, otherUserId),
        unReadMessages = mapOf(ownerId to unreadMessageCount)
    )
}

fun toUpdatePatch(
    uid: String,
    conversationId: String,
    muted: Boolean,
    pinned: Boolean,
    blocked: Boolean,
    deleted: Boolean,
    lastTimeDeleted: Long?,
    otherUserNick: String,
    unreadMessageCount: Int,
    lastDeletedMessageId: String?,
    lastMessageId: String,
    lastSenderId: String,
    lastMessageContent: String,
    lastMessageType: String,
    lastReadBy: Long?
): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    map["id"] = conversationId
    map["isMuted.$uid"] = muted
    map["isPinned.$uid"] = pinned
    map["isBlocked.$uid"] = blocked
    map["isDeleted.$uid"] = deleted
    map["lastTimeDeleted.$uid"] = lastTimeDeleted ?: 0L
    map["nickName.$uid"] = otherUserNick
    map["unReadMessages.$uid"] = unreadMessageCount

    map["lastDeletedMessageId"] = lastDeletedMessageId.orEmpty()
    map["lastMessageId"] = lastMessageId
    map["lastSenderId"] = lastSenderId
    map["lastMessageTime"] = FieldValue.serverTimestamp()
    map["lastMessageContent"] = lastMessageContent
    map["lastMessageType"] = lastMessageType

    map["lastReadBy.$uid"] = lastReadBy ?: FieldValue.serverTimestamp()

    return map

}

fun ConversationDTO.toConversation(
    ownerId: String
): Conversation? {

    val otherUserId = memberIds.firstOrNull { it != ownerId }

    if (otherUserId == null) {
        return null
    }

    return Conversation(
        id = id,
        otherUserNickName = nickName[otherUserId] ?: "",
        ownerNickName = nickName[ownerId] ?: "",
        ownerUserId = ownerId,
        otherUserId = otherUserId,
        createdAt = createdAt?.toMilliseconds(),
        lastMessageId = lastMessageId,
        lastMessageContent = lastMessageContent,
        lastSenderId = lastSenderId,
        lastMessageTime = lastMessageTime,
        lastMessageType = lastMessageType,
        lastDeletedMessageId = lastDeletedMessageId,
        unreadMessageCount = unReadMessages[ownerId] ?: 0,
        deleted = isDeleted[ownerId] ?: false,
        lastTimeDeleted = lastTimeDeleted[ownerId],
        blocked = isBlocked[ownerId] ?: false,
        muted = isMuted[ownerId] ?: false,
        pinned = isPinned[ownerId] ?: false,
    )
}

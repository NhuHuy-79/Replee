package com.nhuhuy.replee.feature_chat.data.mapper

import com.google.firebase.firestore.FieldValue
import com.nhuhuy.replee.core.database.entity.conversation.ConversationAndUser
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.core.network.utils.toMilliseconds
import com.nhuhuy.replee.feature_chat.data.model.network.ConversationDTO
import com.nhuhuy.replee.feature_chat.domain.model.converastion.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType

//Domain save to local
fun Conversation.toConversationEntity(): ConversationEntity {
    return ConversationEntity(
        id = this.id,
        ownerId = this.ownerUserId,
        otherUserId = this.otherUserId,
        createdAt = this.createdAt,
        lastSenderId = this.lastSenderId,
        lastMessageTime = this.lastMessageTime,
        lastMessageContent = this.lastMessageContent,
        unreadMessageCount = this.unreadMessageCount,
        ownerNick = this.ownerNickName,
        otherUserNick = this.otherUserNickName,
        otherUserImg = this.otherUserImg,
        muted = this.muted,
        blocked = this.blocked,
        pinned = this.pinned,
        deleted = this.deleted,
        synced = true,
        lastMessageId = lastMessageId,
        lastTimeSyncs = System.currentTimeMillis(),
        lastMessageType = this.lastMessageType.name,
        lastDeletedMessageId = this.lastDeletedMessageId,
        lastReadBy = this.lastReadBy
    )
}

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
        lastReadBy = mapOf(conversation.otherUserId to conversation.lastReadBy),
        isBlocked = mapOf(conversation.ownerId to conversation.blocked),
        isPinned = mapOf(conversation.ownerId to conversation.pinned),
        isMuted = mapOf(conversation.ownerId to conversation.muted),
        isDeleted = mapOf(conversation.ownerId to conversation.deleted),
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

//Download from network
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
        lastReadBy = lastReadBy[otherUserId],
        unreadMessageCount = unReadMessages[ownerId] ?: 0,
        deleted = isDeleted[ownerId] ?: false,
        blocked = isBlocked[ownerId] ?: false,
        muted = isMuted[ownerId] ?: false,
        pinned = isPinned[ownerId] ?: false,
    )
}

//Map from local to domain
fun ConversationAndUser.toConversation(): Conversation {
    return Conversation(
        id = conversation.id,
        otherUserNickName = conversation.otherUserNick,
        ownerNickName = conversation.ownerNick,
        ownerUserId = conversation.ownerId,
        otherUserId = conversation.otherUserId,
        unreadMessageCount = conversation.unreadMessageCount,
        createdAt = conversation.createdAt,
        lastMessageId = conversation.lastMessageId,
        lastMessageContent = conversation.lastMessageContent,
        lastSenderId = conversation.lastSenderId,
        lastMessageTime = conversation.lastMessageTime,
        lastMessageType = MessageType.valueOf(conversation.lastMessageType),
        lastDeletedMessageId = conversation.lastDeletedMessageId,
        lastReadBy = conversation.lastReadBy,
        muted = conversation.muted,
        pinned = conversation.pinned,
        otherUserName = otherUser?.name.orEmpty(),
        otherUserImg = otherUser?.imageUrl.orEmpty(),
        blocked = conversation.blocked,
        deleted = conversation.deleted,
        otherUserOnline = otherUser?.isOnline ?: false
    )
}


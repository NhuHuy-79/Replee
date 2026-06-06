package com.nhuhuy.replee.core.database.mapper

import com.nhuhuy.replee.core.database.entity.conversation.ConversationAndUser
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.MessageType

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
        lastTimeDeleted = this.lastTimeDeleted,
        synced = true,
        lastMessageId = this.lastMessageId,
        lastMessageType = this.lastMessageType.name,
        lastDeletedMessageId = this.lastDeletedMessageId,
        lastTimeSynced = this.lastSyncedTime,
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
        muted = conversation.muted,
        pinned = conversation.pinned,
        otherUserName = otherUser?.name.orEmpty(),
        otherUserImg = otherUser?.imageUrl.orEmpty(),
        blocked = conversation.blocked,
        deleted = conversation.deleted,
        lastTimeDeleted = conversation.lastTimeDeleted,
        lastSyncedTime = conversation.lastTimeSynced,
        otherUserOnline = otherUser?.isOnline ?: false
    )
}

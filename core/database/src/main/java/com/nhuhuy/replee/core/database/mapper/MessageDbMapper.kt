package com.nhuhuy.replee.core.database.mapper

import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.core.model.Message
import com.nhuhuy.replee.core.model.MessageStatus
import com.nhuhuy.replee.core.model.MessageType

fun MessageEntity.toMessage() : Message{
    return Message(
        conversationId = conversationId,
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        deleted = deleted,
        content = content,
        pinned = pinned,
        sentAt = sentAt ?: System.currentTimeMillis(),
        type = MessageType.valueOf(type),
        status = MessageStatus.valueOf(this.status),
        localUriPath = localUriPath,
        remoteUrl = remoteUrl,
        repliedMessageContent = repliedMessageContent,
        repliedMessageId = repliedMessageId,
        repliedMessageSenderId = repliedMessageSenderId,
        repliedMessageType = repliedMessageType?.let { MessageType.valueOf(it) },
        repliedMessageRemoteUrl = repliedMessageRemoteUrl,
        ownerReactions = ownerReactions,
        otherUserReactions = otherUserReactions
    )
}

fun Message.toMessageEntity() : MessageEntity{
    return MessageEntity(
        conversationId = conversationId,
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        sentAt = sentAt,
        status = status.name,
        type = type.name,
        deleted = deleted,
        localUriPath = localUriPath,
        remoteUrl = remoteUrl,
        repliedMessageContent = repliedMessageContent,
        repliedMessageId = repliedMessageId,
        repliedMessageSenderId = repliedMessageSenderId,
        repliedMessageType = repliedMessageType?.name,
        repliedMessageRemoteUrl = repliedMessageRemoteUrl,
        pinned = pinned,
        ownerReactions = ownerReactions,
        otherUserReactions = otherUserReactions
    )
}

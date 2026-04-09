package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.core.network.utils.toMilliseconds
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType

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
        repliedMessageRemoteUrl = repliedMessageRemoteUrl
    )
}

fun MessageDTO.toMessage() : Message{
    return Message(
        conversationId = conversationId,
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        deleted = false,
        pinned = pinned,
        sentAt = sendAt?.toMilliseconds() ?: System.currentTimeMillis(),
        status = status,
        type = type,
        remoteUrl = url,
        repliedMessageContent = repliedMessageContent,
        repliedMessageId = repliedMessageId,
        repliedMessageSenderId = repliedMessageSenderId,
        repliedMessageType = repliedMessageType,
        repliedMessageRemoteUrl = repliedMessageRemoteUrl
    )
}

fun Message.toMessageDTO() : MessageDTO {
    return MessageDTO(
        conversationId = conversationId,
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        pinned = pinned,
        type = type,
        url = remoteUrl,
        status = status,
        repliedMessageId = repliedMessageId,
        repliedMessageContent = repliedMessageContent,
        repliedMessageSenderId = repliedMessageSenderId,
        repliedMessageType = repliedMessageType,
        repliedMessageRemoteUrl = repliedMessageRemoteUrl
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
        pinned = pinned
    )
}

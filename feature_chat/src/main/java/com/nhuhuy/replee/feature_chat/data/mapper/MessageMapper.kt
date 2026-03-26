package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.core.network.utils.toMilliseconds
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType

fun MessageEntity.toMessage() : Message{
    return Message(
        conversationId = conversationId,
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        seen = seen,
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
        seen = seen,
        sentAt = sendAt?.toMilliseconds() ?: System.currentTimeMillis(),
        status = MessageStatus.SYNCED,
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
        seen = seen,
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
        seen = seen,
        sentAt = sentAt,
        status = status.name,
        type = type.name,
        localUriPath = localUriPath,
        remoteUrl = remoteUrl,
        repliedMessageContent = repliedMessageContent,
        repliedMessageId = repliedMessageId,
        repliedMessageSenderId = repliedMessageSenderId,
        repliedMessageType = repliedMessageType?.name,
        repliedMessageRemoteUrl = repliedMessageRemoteUrl
    )
}

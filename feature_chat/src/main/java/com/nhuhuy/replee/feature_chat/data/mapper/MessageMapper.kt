package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.database.entity.message.MessageEntity
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
        sentAt = sentAt,
        type = MessageType.valueOf(type),
        status = MessageStatus.valueOf(this.status)
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
        sentAt = sendAt,
        status = MessageStatus.SYNCED,
        type = type
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
        sendAt = sentAt,
        type = type,
        status = status
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
        type = type.name
    )
}

package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.feature_chat.data.model.network.MessageDTO
import com.nhuhuy.replee.feature_chat.domain.model.Message

fun MessageEntity.toMessage() : Message{
    return Message(
        conversationId = conversationId,
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        seen = seen,
        sentAt = sentAt,
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
        sendAt = sentAt
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
        sentAt = sentAt
    )
}


class MessageMapper : BaseMapper<MessageDTO, Message, MessageEntity>{
    override fun fromRemoteToDomain(remote: MessageDTO): Message {
        return Message(
            conversationId = remote.conversationId,
            messageId = remote.messageId,
            senderId = remote.senderId,
            receiverId = remote.receiverId,
            content = remote.content,
            seen = remote.seen,
            sentAt = remote.sendAt
        )
    }

    override fun fromRemoteToLocal(local: MessageEntity): Message {
        TODO("Not yet implemented")
    }

    override fun fromLocalToDomain(local: MessageEntity): Message {
        TODO("Not yet implemented")
    }

    override fun fromDomainToRemote(domain: Message): MessageDTO {
        return MessageDTO(
            conversationId = domain.conversationId,
            messageId = domain.messageId,
            senderId = domain.senderId,
            receiverId = domain.receiverId,
            content = domain.content,
            seen = domain.seen,
            sendAt = domain.sentAt
        )
    }

}
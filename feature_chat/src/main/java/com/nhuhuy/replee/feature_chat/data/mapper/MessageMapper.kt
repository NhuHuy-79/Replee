package com.nhuhuy.replee.feature_chat.data.mapper

import com.google.firebase.messaging.remoteMessage
import com.nhuhuy.replee.core.firebase.utils.toMilliseconds
import com.nhuhuy.replee.feature_chat.data.model.MessageDTO
import com.nhuhuy.replee.feature_chat.data.model.local.MessageEntity
import com.nhuhuy.replee.feature_chat.domain.model.Message

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
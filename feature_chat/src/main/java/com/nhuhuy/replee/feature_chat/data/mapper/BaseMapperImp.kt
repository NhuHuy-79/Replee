package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.firebase.utils.toMilliseconds
import com.nhuhuy.replee.feature_chat.data.model.ConversationDTO
import com.nhuhuy.replee.feature_chat.data.model.ConversationDTOUser
import com.nhuhuy.replee.feature_chat.data.model.ConversationEntity
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.ConversationOtherUser

class ConversationMapper() : BaseMapper<ConversationDTO, Conversation, ConversationEntity>{
    fun ConversationDTOUser.toConversationOtherUser() : ConversationOtherUser {
        return ConversationOtherUser(
            uid = uid,
            name = name
        )
    }
    override fun fromRemoteToDomain(remote: ConversationDTO): Conversation {
        return Conversation(
            id = remote.id,
            members = remote.membersId.map { it.toConversationOtherUser() },
            createdAt = remote.createdAt?.toMilliseconds(),
            lastMessageContent = remote.lastMessageContent,
            lastSenderId = remote.lastSenderId,
            lastMessageTime = remote.lastMessageTime?.toMilliseconds()
        )
    }


    override fun fromRemoteToLocal(local: ConversationEntity): Conversation {
        TODO("Not yet implemented")
    }

    override fun fromLocalToDomain(local: ConversationEntity): Conversation {
        TODO("Not yet implemented")
    }

    override fun fromDomainToRemote(domain: Conversation): ConversationDTO {
        TODO("Not yet implemented")
    }

}
package com.nhuhuy.replee.core.network.mapper

import com.nhuhuy.replee.core.model.Message
import com.nhuhuy.replee.core.model.MessageStatus
import com.nhuhuy.replee.core.model.MessageType
import com.nhuhuy.replee.core.network.model.MessageDTO
import com.nhuhuy.replee.core.network.utils.toMilliseconds

fun MessageDTO.toMessage(currentUserId: String? = null): Message {
    val ownerReactions = currentUserId?.let { reactions[it] } ?: emptyList()
    val otherUserReactions = reactions.filter { it.key != currentUserId }.values.flatten()

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
        repliedMessageRemoteUrl = repliedMessageRemoteUrl,
        ownerReactions = ownerReactions,
        otherUserReactions = otherUserReactions
    )
}

fun Message.toMessageDTO(currentUserId: String? = null): MessageDTO {
    val reactionsMap = mutableMapOf<String, List<String>>()
    currentUserId?.let { currentUserId ->
        val otherUserId = if (senderId == currentUserId) receiverId else senderId
        reactionsMap[currentUserId] = ownerReactions
        reactionsMap[otherUserId] = otherUserReactions
    }

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
        repliedMessageRemoteUrl = repliedMessageRemoteUrl,
        reactions = reactionsMap
    )
}

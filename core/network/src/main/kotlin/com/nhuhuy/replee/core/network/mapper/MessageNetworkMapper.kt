package com.nhuhuy.replee.core.network.mapper

import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.network.model.MessageDTO
import com.nhuhuy.replee.core.network.utils.toMilliseconds

fun MessageDTO.toMessage(currentUserId: String? = null): Message {
    val safeReactions = reactions
    val ownerReactions = currentUserId?.let { safeReactions[it] } ?: emptyList()
    val otherUserReactions = mutableListOf<String>()

    safeReactions.forEach { (userId, reactionList) ->
        if (userId != currentUserId && reactionList != null) {
            otherUserReactions.addAll(reactionList.filterNotNull())
        }
    }

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
        ownerReactions = ownerReactions.filterNotNull(),
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

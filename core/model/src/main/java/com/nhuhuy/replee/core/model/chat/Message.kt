package com.nhuhuy.replee.core.model.chat

data class Message(
    val conversationId: String,
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val sentAt: Long,
    val deleted: Boolean = false,
    val status: MessageStatus,
    val pinned: Boolean = false,
    val type: MessageType = MessageType.TEXT,
    val remoteUrl: String? = null,
    val localUriPath: String? = null,
    val repliedMessageContent: String? = null,
    val repliedMessageId: String? = null,
    val repliedMessageSenderId: String? = null,
    val repliedMessageType: MessageType? = null,
    val repliedMessageRemoteUrl: String? = null,
    //update 1 map <UserId, Reactions> to list reactions
    val ownerReactions: List<String> = emptyList(),
    val otherUserReactions: List<String> = emptyList()
) {
    val reactions: Map<String, Int>
        get() =
            ownerReactions.groupBy { it }.mapValues { it.value.size } +
                    otherUserReactions.groupBy { it }.mapValues { it.value.size }
}

enum class MessageType {
    TEXT,
    IMAGE,

}

enum class MessageStatus {
    SYNCED,
    PENDING,
    FAILED,
    SEEN,
}

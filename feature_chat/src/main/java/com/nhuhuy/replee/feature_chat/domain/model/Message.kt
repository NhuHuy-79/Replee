package com.nhuhuy.replee.feature_chat.domain.model

data class Message(
    val conversationId: String,
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val seen: Boolean,
    val sentAt: Long,
    val status: MessageStatus,
    val type: MessageType = MessageType.TEXT,
    val remoteUrl: String? = null,
    val localUriPath: String? = null,
)

enum class MessageType {
    TEXT,
    IMAGE,

}
enum class MessageStatus{
    SYNCED,
    PENDING,
    FAILED,
    SEEN,
}
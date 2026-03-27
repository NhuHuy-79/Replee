package com.nhuhuy.replee.feature_chat.domain.model.message

sealed interface MessageAction {
    data class Delete(val messageId: String = "") : MessageAction
    data class MarkAsRead(val conversationId: String = "") : MessageAction
    data class Edit(val messageId: String, val content: String) : MessageAction
}





package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.model.chat.Message

sealed interface MessageAction : UiAction {
    data class JumpToMessageId(val messageId: String) : MessageAction
    data object ScrollToTop : MessageAction
    data object ScrollToBottom : MessageAction
    data object OnDismiss : MessageAction
    data class OnImagePress(val urlKey: String) : MessageAction
    data class OnMessageLongPress(val message: Message) : MessageAction
    data object OnMessageReply : MessageAction
    data object OnMessageDelete : MessageAction
    data object OnMessagePin : MessageAction
    data object OnMessageUnPin : MessageAction
    data class OnReactionSelect(val reaction: String) : MessageAction
    data class OnReactionDelete(val messageId: String, val reaction: String) : MessageAction
    data object OnReactionMoreClick : MessageAction
    data class OnMessageReactionClick(val messageId: String, val reaction: String) : MessageAction
}

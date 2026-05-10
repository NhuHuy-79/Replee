package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.model.chat.Message

sealed interface MessageContentAction : UiAction {
    data class JumpToMessageContentId(val messageId: String) : MessageContentAction
    data object JumpToBottom : MessageContentAction
    data object ScrollToTop : MessageContentAction
    data object ScrollToBottom : MessageContentAction
    data object OnDismiss : MessageContentAction
    data class OnImagePress(val urlKey: String) : MessageContentAction
    data class OnMessageContentLongPress(val message: Message) : MessageContentAction
    data object OnMessageContentReply : MessageContentAction
    data object OnMessageContentDelete : MessageContentAction
    data object OnMessageContentPin : MessageContentAction
    data object OnMessageContentUnPin : MessageContentAction
    data class OnReactionSelect(val reaction: String) : MessageContentAction
    data class OnReactionDelete(val messageId: String, val reaction: String) : MessageContentAction
    data object OnReactionMoreClick : MessageContentAction
    data class OnMessageContentReactionClick(val messageId: String, val reaction: String) :
        MessageContentAction
}

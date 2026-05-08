package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content

import com.nhuhuy.replee.core.common.base.UiAction

sealed interface MessageAction : UiAction {
    data class JumpToMessageId(val messageId: String) : MessageAction
    data object ScrollToTop : MessageAction
    data object ScrollToBottom : MessageAction
}
package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input

import android.net.Uri
import com.nhuhuy.replee.core.common.base.UiAction

sealed interface MessageInputAction : UiAction {
    data class OnMessageInputChange(val text: String) : MessageInputAction
    data class OnImageSelect(val uri: Uri) : MessageInputAction
    data object OnReplyRemove : MessageInputAction
    data object OnSendButtonClick : MessageInputAction
    data object OnTypingTrigger : MessageInputAction
}

package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input

import android.net.Uri
import com.nhuhuy.replee.core.common.base.UiAction

sealed interface MessageInputAction : UiAction {
    data object OnSendButtonClick : MessageInputAction
    data class OnImageSelect(val uri: Uri) : MessageInputAction
    data class OnMessageInputChange(val text: String) : MessageInputAction
    data object OnReplyRemove : MessageInputAction
}
package com.nhuhuy.replee.feature_chat.presentation.chat.state

import android.net.Uri
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.feature_chat.domain.model.Message

sealed interface ChatAction : UiAction{
    data object OnMessageReply : ChatAction
    data object OnMessageCancelReply : ChatAction
    data class OnMessageLongPress(val message: Message) : ChatAction
    data class OnMessageInputChanged(val messageInput: String) : ChatAction
    object OnSendMessageClicked : ChatAction
    data class OnImageSend(val uri: Uri) : ChatAction
    object OnBackClick: ChatAction
    data object OnUnblockUser : ChatAction
    data object OnMoreClick: ChatAction
    data object OnDismiss : ChatAction
    data class OnImagePress(val urlKey: String) : ChatAction
    data object OnMessageDelete : ChatAction
    data object OnMessagePin : ChatAction

}
package com.nhuhuy.replee.feature_chat.presentation.chat.state

import com.nhuhuy.replee.core.common.base.UiAction

sealed interface ChatAction : UiAction{
    data class OnMessageInputChanged(val messageInput: String) : ChatAction
    object OnSendMessageClicked : ChatAction
    object OnBackClick: ChatAction
    data class OnReadMessage(val ids: Set<String>) : ChatAction
    data object OnUnblockUser : ChatAction
    data object OnMoreClick: ChatAction

    data class OnMessageDelete(val id: String) : ChatAction
    data class OnMessagePin(val id: String) : ChatAction

}
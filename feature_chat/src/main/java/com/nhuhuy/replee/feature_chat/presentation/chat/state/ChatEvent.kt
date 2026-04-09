package com.nhuhuy.replee.feature_chat.presentation.chat.state

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface ChatEvent : UiEvent{
    data class NavigateToSearch(val conversationId: String, val otherUserId: String) : ChatEvent
    data class NavigateToPin(
        val conversationId: String,
        val otherUserId: String
    ) : ChatEvent
    data object NavigateBack : ChatEvent

    sealed interface SendImage : ChatEvent {
        data object Success : SendImage
        data object Failure : SendImage
    }

    data object FileTooLarge : ChatEvent
    data object UnSupportedFile : ChatEvent
    data object Unknown : ChatEvent
    data class NavigateToInformation(
        val otherUserImg: String,
        val currentUserId: String,
        val conversationId: String,
        val otherUserId: String,
        val otherUserName: String,
        val otherUserEmail: String
    ): ChatEvent
}
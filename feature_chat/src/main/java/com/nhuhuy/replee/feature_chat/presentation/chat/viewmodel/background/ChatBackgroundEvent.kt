package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface ChatBackgroundEvent : UiEvent {
    data object NavigateBack : ChatBackgroundEvent
    data class NavigateToInformation(
        val currentUserId: String,
        val conversationId: String,
        val otherUserId: String,
    ) : ChatBackgroundEvent

    data class NavigateToSearch(
        val conversationId: String,
        val otherUserId: String,
        val currentUserId: String
    ) : ChatBackgroundEvent

    data class NavigateToPin(
        val conversationId: String,
        val otherUserId: String,
        val currentUserId: String
    ) : ChatBackgroundEvent
}

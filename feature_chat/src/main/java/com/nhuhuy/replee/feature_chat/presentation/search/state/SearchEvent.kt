package com.nhuhuy.replee.feature_chat.presentation.search.state

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface SearchEvent : UiEvent {
    data object NavigateBack : SearchEvent
    data class NavigateToMessage(
        val currentUserId: String,
        val anchorSendAt: Long,
        val anchorMessageId: String
    ) : SearchEvent
}
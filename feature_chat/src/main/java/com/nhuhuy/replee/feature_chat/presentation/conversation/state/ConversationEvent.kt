package com.nhuhuy.replee.feature_chat.presentation.conversation.state

import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure

sealed interface ConversationEvent : UiEvent {
    data class NavigateToChatRoom(
        val conversationId: String,
        val currentUserId: String,
        val otherUserId: String,
    )  : ConversationEvent
    data class Error(val error: RemoteFailure) : ConversationEvent
    data object GoToProfile : ConversationEvent
}
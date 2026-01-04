package com.nhuhuy.replee.feature_chat.presentation.conversation.state

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface ConversationEvent : UiEvent {
    data class NavigateToChatRoom(val conversationId: String)  : ConversationEvent

    data object GoToProfile : ConversationEvent
}
package com.nhuhuy.replee.feature_chat.presentation.pin

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.feature_chat.domain.model.message.Message

data class PinState(
    val currentUser: Account = Account(),
    val otherUser: Account = Account(),
) : UiState

sealed interface PinAction : UiAction {
    data object OnBackPress : PinAction
    data class OnPinOff(val message: Message) : PinAction
    data class OnMessageClick(val message: Message) : PinAction
}

sealed interface PinEvent : UiEvent {
    data object NavigateBack : PinEvent
    data class NavigateToConversation(
        val otherUserId: String,
        val currentUserId: String,
        val messageId: String
    ) : PinEvent
}

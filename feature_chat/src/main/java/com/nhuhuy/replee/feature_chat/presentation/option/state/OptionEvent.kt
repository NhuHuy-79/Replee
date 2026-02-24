package com.nhuhuy.replee.feature_chat.presentation.option.state

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface OptionEvent : UiEvent {
    data object NavigateBack : OptionEvent
    data object NavigateToConversation : OptionEvent
}
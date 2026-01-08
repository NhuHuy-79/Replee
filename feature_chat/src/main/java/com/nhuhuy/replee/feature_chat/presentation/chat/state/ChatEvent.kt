package com.nhuhuy.replee.feature_chat.presentation.chat.state

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface ChatEvent : UiEvent{
    data object NavigateBack : ChatEvent
    data object NavigateToInformation: ChatEvent
}
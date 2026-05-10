package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface MessageContentEvent : UiEvent {
    data object ScrollToAnchor : MessageContentEvent
}

package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface MessageEvent : UiEvent {
    data object ScrollToAnchor : MessageEvent
}

package com.nhuhuy.replee.feature_chat.presentation.search.state

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface SearchEvent : UiEvent {
    data object NavigateBack : SearchEvent
}
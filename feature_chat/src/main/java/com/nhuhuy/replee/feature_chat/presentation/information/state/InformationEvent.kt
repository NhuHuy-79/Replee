package com.nhuhuy.replee.feature_chat.presentation.information.state

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface InformationEvent : UiEvent {
    data object NavigateBack : InformationEvent
}
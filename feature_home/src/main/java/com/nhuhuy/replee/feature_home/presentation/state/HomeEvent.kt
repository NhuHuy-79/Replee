package com.nhuhuy.replee.feature_home.presentation.state

import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.error.RemoteFailure

sealed interface HomeEvent : UiEvent {
    data class NavigateToChatRoom(
        val currentUserId: String,
        val otherUserId: String,
    ) : HomeEvent

    data class Error(val error: RemoteFailure) : HomeEvent
    data object GoToProfile : HomeEvent
}

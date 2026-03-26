package com.nhuhuy.replee.feature_auth.presentation.sign_up

import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.error.RemoteFailure

sealed interface SignUpEvent : UiEvent {
    data object NavigateBack : SignUpEvent
    data class NavigateToHome(val uid: String) : SignUpEvent
    data class Failure(val error: RemoteFailure): SignUpEvent
}
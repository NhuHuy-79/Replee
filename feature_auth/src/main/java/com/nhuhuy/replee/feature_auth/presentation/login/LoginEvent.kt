package com.nhuhuy.replee.feature_auth.presentation.login

import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure

sealed interface LoginEvent : UiEvent{
    data object NavigateToRecover : LoginEvent
    data object NavigateToSignUp: LoginEvent
    data object NavigateToHome: LoginEvent
    data class Failure(val error: RemoteFailure) : LoginEvent
}
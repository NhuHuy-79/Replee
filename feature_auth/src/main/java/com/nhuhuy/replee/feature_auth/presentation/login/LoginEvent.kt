package com.nhuhuy.replee.feature_auth.presentation.login

import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.data.model.RemoteFailure
import com.nhuhuy.replee.feature_auth.data.GoogleCredentialResult

sealed interface LoginEvent : UiEvent{
    data object NavigateToRecover : LoginEvent
    data object NavigateToSignUp: LoginEvent
    data object NavigateToHome: LoginEvent
    data class Failure(val error: RemoteFailure) : LoginEvent
    data class GoogleErrorSnackBar(val error: GoogleCredentialResult) : LoginEvent
}
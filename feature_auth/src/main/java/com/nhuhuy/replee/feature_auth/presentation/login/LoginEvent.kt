package com.nhuhuy.replee.feature_auth.presentation.login

import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.error.RemoteFailure
import com.nhuhuy.replee.feature_auth.data.model.GoogleCredentialResult

sealed interface LoginEvent : UiEvent{
    data object NavigateToRecover : LoginEvent
    data object NavigateToSignUp: LoginEvent
    data class NavigateToHome(val uid: String) : LoginEvent
    data class Failure(val error: RemoteFailure) : LoginEvent
    data class GoogleErrorSnackBar(val error: GoogleCredentialResult) : LoginEvent
}
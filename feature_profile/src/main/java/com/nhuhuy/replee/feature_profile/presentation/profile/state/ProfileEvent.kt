package com.nhuhuy.replee.feature_profile.presentation.profile.state

import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.data.model.RemoteFailure

sealed interface ProfileEvent : UiEvent{
    data object GoToAbout: ProfileEvent

    data object GoToSignIn: ProfileEvent
    data object NavigateBack : ProfileEvent

    sealed interface UpdatePassword : ProfileEvent {
        data object Success : UpdatePassword
        data class Failure(val error: RemoteFailure) : UpdatePassword
    }

    sealed interface UpdateAvatar : ProfileEvent {
        data object Success : UpdateAvatar
        data object Failure : UpdateAvatar
    }
}


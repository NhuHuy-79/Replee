package com.nhuhuy.replee.feature_profile.presentation.profile.state

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface ProfileEvent : UiEvent{
    data object None: ProfileEvent
    data object GoToAbout: ProfileEvent

    data object ShowNotificationDialog: ProfileEvent
    data object ShowThemeDialog: ProfileEvent
}


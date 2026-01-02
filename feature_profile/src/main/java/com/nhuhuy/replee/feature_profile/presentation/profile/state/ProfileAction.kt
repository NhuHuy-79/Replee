package com.nhuhuy.replee.feature_profile.presentation.profile.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.feature_profile.data.data_store.NotificationMode
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode

sealed interface ProfileAction : UiAction {
    data object OnAboutClick: ProfileAction
    sealed interface OnDarkModeClick : ProfileAction {
        data class Select(val option: ThemeMode): OnDarkModeClick
        data object Dialog: OnDarkModeClick
    }
    sealed interface OnNotificationClick: ProfileAction {
        data class Select(val option: NotificationMode) : OnNotificationClick
        data object Dialog: OnNotificationClick
    }
}
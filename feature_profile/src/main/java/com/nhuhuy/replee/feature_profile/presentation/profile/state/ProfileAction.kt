package com.nhuhuy.replee.feature_profile.presentation.profile.state

import android.net.Uri
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.data.data_store.NotificationMode
import com.nhuhuy.replee.core.common.data.data_store.ThemeMode

sealed interface ProfileAction : UiAction {

    data object OnDismiss: ProfileAction
    data object OnAboutClick: ProfileAction
    sealed interface OnDarkModeClick : ProfileAction {
        data class Select(val option: ThemeMode): OnDarkModeClick
        data object Dialog: OnDarkModeClick
    }
    sealed interface OnNotificationClick: ProfileAction {
        data class Select(val option: NotificationMode) : OnNotificationClick
        data object Dialog: OnNotificationClick
    }

    sealed interface OnUpdatePassword : ProfileAction {
        data object BottomSheet : OnUpdatePassword
        data object Confirm : OnUpdatePassword
    }

    sealed interface OnPhotoPicker : ProfileAction {
        data object Launcher : OnPhotoPicker
        data class Select(val uri: Uri) : OnPhotoPicker
    }

    data object OnLogOut : ProfileAction

    data object OnNavigateBack : ProfileAction
    data class OnNewPasswordChange(val password: String) : ProfileAction
    data class OnOldPasswordChange(val password: String) : ProfileAction

    data object OnEditDialogOpen : ProfileAction
}
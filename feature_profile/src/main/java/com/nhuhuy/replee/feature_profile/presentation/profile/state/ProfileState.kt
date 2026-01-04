package com.nhuhuy.replee.feature_profile.presentation.profile.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.design_system.component.DynamicInput
import com.nhuhuy.replee.feature_profile.data.data_store.NotificationMode
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode

@Immutable
data class ProfileState(
    val account: Account = Account(),
    val notification: NotificationMode = NotificationMode.NONE,
    val darkMode: ThemeMode = ThemeMode.DEFAULT,
    val oldPassword: DynamicInput = DynamicInput(),
    val newPassword: DynamicInput = DynamicInput(),
    val overlay: Overlay = Overlay.NONE,
) : UiState {
    val valid get() = oldPassword.valid && newPassword.valid
}


enum class Overlay {
    NONE,
    THEME,
    NOTIFICATION,
    UPDATE_PASSWORD
}

package com.nhuhuy.replee.feature_profile.presentation.profile.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.design_system.component.ValidatableInput
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.feature_profile.data.data_store.NotificationMode
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode

@Immutable
data class ProfileState(
    val account: Account = Account(),
    val notification: NotificationMode = NotificationMode.NONE,
    val darkMode: ThemeMode = ThemeMode.DEFAULT,
    val oldPassword: ValidatableInput = ValidatableInput(),
    val newPassword: ValidatableInput = ValidatableInput(),
    val avatarUploading: Boolean = false,
    val overlay: Overlay = Overlay.NONE,
) : UiState {
    val valid get() = oldPassword.valid && newPassword.valid

}

@Immutable
data class ProfileActionResult(
    val updatePassword: ScreenState<Unit> = ScreenState.Idle,
    val updateAvatarLoading: Boolean = false
)


enum class Overlay {
    NONE,
    THEME,
    OPTIONS,
    IMAGE_PICKER,
    NOTIFICATION,
    UPDATE_PASSWORD
}

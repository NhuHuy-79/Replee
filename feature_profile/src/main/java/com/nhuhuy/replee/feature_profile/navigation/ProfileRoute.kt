package com.nhuhuy.replee.feature_profile.navigation

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.common.utils.showShortToast
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.feature_profile.R
import com.nhuhuy.replee.feature_profile.presentation.ProfileScreen
import com.nhuhuy.replee.feature_profile.presentation.ProfileViewModel
import com.nhuhuy.replee.feature_profile.presentation.profile.component.EditDialog
import com.nhuhuy.replee.feature_profile.presentation.profile.component.NotificationDialog
import com.nhuhuy.replee.feature_profile.presentation.profile.component.SelectThemeDialog
import com.nhuhuy.replee.feature_profile.presentation.profile.component.UpdatePasswordSheet
import com.nhuhuy.replee.feature_profile.presentation.profile.state.Overlay
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction.OnNewPasswordChange
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction.OnOldPasswordChange
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction.OnUpdatePassword
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent
import timber.log.Timber

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToAbout: () -> Unit,
) = BoxContainer {
    val context: Context = LocalContext.current
    val profileActionResult by viewModel.profileActionResult.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onAction(ProfileAction.OnPhotoPicker.Select(uri))
        } else {
            Timber.e("No media selected")
        }
    }

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            ProfileEvent.GoToAbout -> onNavigateToAbout()
            is ProfileEvent.UpdatePassword.Failure -> {
                // Handle failure if needed, e.g., show a snackbar or toast
            }

            ProfileEvent.UpdatePassword.Success -> {
                context.showShortToast(R.string.update_password_success)
            }

            ProfileEvent.GoToSignIn -> onNavigateToSignIn()

            ProfileEvent.NavigateBack -> onNavigateBack()

            ProfileEvent.UpdateAvatar.Failure -> {
                context.showShortToast(R.string.update_avatar_failed)
            }

            ProfileEvent.UpdateAvatar.Success -> {
                context.showShortToast(R.string.update_avatar_success)
            }
        }
    }

    ProfileScreen(
        state = state,
        onAction = onAction,
        profileActionResult = profileActionResult
    )

    when (state.overlay) {
        Overlay.NONE -> Unit
        Overlay.THEME -> SelectThemeDialog(
            modifier = Modifier,
            currentThemeMode = state.darkMode,
            onThemeModeSelect = { theme ->
                onAction(ProfileAction.OnDarkModeClick.Select(theme))
            },
            onDismiss = { onAction(ProfileAction.OnDismiss) }
        )

        Overlay.NOTIFICATION -> NotificationDialog(
            currentOption = state.notification,
            onNotificationSelect = { notification ->
                onAction(ProfileAction.OnNotificationClick.Select(notification))
            },
            onDismiss = { onAction(ProfileAction.OnDismiss) },
        )

        Overlay.UPDATE_PASSWORD -> UpdatePasswordSheet(
            result = profileActionResult.updatePassword,
            state = state,
            onOldPasswordChange = { value ->
                onAction(OnOldPasswordChange(value))
            },
            onNewPasswordChange = { value ->
                onAction(OnNewPasswordChange(value))
            },
            onConfirm = { onAction(OnUpdatePassword.Confirm) },
            onDismiss = { onAction(ProfileAction.OnDismiss) },
        )

        Overlay.OPTIONS -> {
            EditDialog(
                onDismiss = { onAction(ProfileAction.OnDismiss) },
                onEditPassword = { onAction(OnUpdatePassword.BottomSheet) },
                onEditAvatar = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }

        Overlay.IMAGE_PICKER -> {

        }
    }
}

package com.nhuhuy.replee.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.core.common.utils.showShortToast
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.feature_profile.R
import com.nhuhuy.replee.feature_profile.presentation.ProfileScreen
import com.nhuhuy.replee.feature_profile.presentation.ProfileViewModel
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent
import kotlinx.serialization.Serializable

@Serializable
sealed interface ProfileDestination : NavKey {
    @Serializable
    data object Profile: ProfileDestination
}

@SuppressLint("ShowToast")
fun EntryProviderScope<NavKey>.profileGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<ProfileDestination.Profile>{
        val context: Context = LocalContext.current
        val viewModel: ProfileViewModel = hiltViewModel()
        val profileActionResult by viewModel.profileActionResult.collectAsStateWithLifecycle()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val event = viewModel.event

        ObserveEffect(event) { event ->
            when (event) {
                ProfileEvent.GoToAbout -> {}
                is ProfileEvent.UpdatePassword.Failure -> {}
                ProfileEvent.UpdatePassword.Success -> {
                    context.showShortToast(R.string.update_password_success)
                }
                ProfileEvent.GoToSignIn -> {
                    backstack.clear()
                    backstack.add(AuthDestination.Login)
                }

                ProfileEvent.NavigateBack -> {
                    backstack.removeLastOrNull()
                }

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
            onAction = viewModel::onAction,
            profileActionResult = profileActionResult
        )
    }
}
package com.nhuhuy.replee.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.feature_profile.presentation.ProfileScreen
import com.nhuhuy.replee.feature_profile.presentation.ProfileViewModel
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent
import kotlinx.serialization.Serializable

@Serializable
sealed interface ProfileDestination : NavKey {
    @Serializable
    data object Profile: ProfileDestination
}

fun EntryProviderScope<NavKey>.profileGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<ProfileDestination.Profile>{
        val viewModel: ProfileViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val event = viewModel.event

        ObserveEffect(event) { event ->
            when (event) {
                ProfileEvent.GoToAbout -> {}
                is ProfileEvent.UpdatePassword.Failure -> {}
                ProfileEvent.UpdatePassword.Success -> {}
                ProfileEvent.GoToSignIn -> {
                    backstack.add(AuthDestination.Login)
                    backstack.remove(ProfileDestination.Profile)
                }
            }
        }

        ProfileScreen(
            state = state,
            onAction = viewModel::onAction
        )
    }
}
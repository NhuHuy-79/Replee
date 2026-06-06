package com.nhuhuy.replee.navigation

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.feature_profile.navigation.ProfileRoute
import com.nhuhuy.replee.feature_profile.presentation.ProfileViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface ProfileDestination : NavKey {
    @Serializable
    data object Profile : ProfileDestination
}

@SuppressLint("ShowToast")
fun EntryProviderScope<NavKey>.profileGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<ProfileDestination.Profile> {
        val viewModel: ProfileViewModel = hiltViewModel()

        ProfileRoute(
            viewModel = viewModel,
            onNavigateBack = backstack::removeLastOrNull,
            onNavigateToSignIn = {
                backstack.clear()
                backstack.add(AuthDestination.Login)
            },
            onNavigateToAbout = {
                // Handle navigation to about
            }
        )
    }
}

package com.nhuhuy.replee.navigation.splash

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SplashKey : NavKey

fun EntryProviderScope<NavKey>.splashGraph(
    navigateToHome: (uid: String) -> Unit,
    navigateToLogin: () -> Unit
) {
    entry<SplashKey> {
        val splashViewModel: SplashViewModel = hiltViewModel()
        val authenticatedState by splashViewModel.authenticatedState.collectAsStateWithLifecycle()
        SplashScreen(
            navigateToHome = navigateToHome,
            navigateToLogin = navigateToLogin,
            authenticatedState = authenticatedState
        )
    }
}
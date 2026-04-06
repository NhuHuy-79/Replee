package com.nhuhuy.replee.navigation.splash

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
        SplashScreen(
            navigateToHome = navigateToHome,
            navigateToLogin = navigateToLogin,
        )
    }
}
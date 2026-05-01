package com.nhuhuy.replee.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.nhuhuy.replee.feature_auth.navigation.ForgotPasswordRoute
import com.nhuhuy.replee.feature_auth.navigation.LoginRoute
import com.nhuhuy.replee.feature_auth.navigation.SignUpRoute
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthDestination : NavKey {
    @Serializable
    data object Login : AuthDestination
    @Serializable
    data object SignUp : AuthDestination
    @Serializable
    data object ForgotPassword : AuthDestination
}

private const val DURATION: Int = 350
private val AUTH_METADATA_TRANSITION = NavDisplay.transitionSpec {
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(DURATION)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(DURATION)
    )
} + NavDisplay.popTransitionSpec {
    slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(DURATION)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(DURATION)
    )
} + NavDisplay.predictivePopTransitionSpec {
    slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(DURATION)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(DURATION)
    )
}

fun EntryProviderScope<NavKey>.authGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<AuthDestination.Login>(
        metadata = AUTH_METADATA_TRANSITION
    ) { _ ->
        LoginRoute(
            viewModel = hiltViewModel(),
            onNavigateToForgotPassword = {
                backstack.add(AuthDestination.ForgotPassword)
            },
            onNavigateToHome = { uid ->
                backstack.clear()
                backstack.add(
                    HomeDestination.ConversationList(
                        currentUserId = uid
                    )
                )
            },
            onNavigateToSignUp = {
                backstack.add(AuthDestination.SignUp)
            },
        )
    }

    entry<AuthDestination.SignUp>(
        metadata = AUTH_METADATA_TRANSITION
    ) {
        SignUpRoute(
            viewModel = hiltViewModel(),
            onNavigateBack = backstack::removeLastOrNull,
            onNavigateToHome = { uid ->
                backstack.clear()
                backstack.add(
                    HomeDestination.ConversationList(
                        currentUserId = uid
                    )
                )
            }
        )

    }

    entry<AuthDestination.ForgotPassword>(
        metadata = AUTH_METADATA_TRANSITION
    ) {
        ForgotPasswordRoute(
            onNavigateBack = backstack::removeLastOrNull,
            viewModel = hiltViewModel()
        )
    }
}

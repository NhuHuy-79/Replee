package com.nhuhuy.replee.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.nhuhuy.replee.feature_auth.R
import com.nhuhuy.replee.feature_auth.presentation.login.LoginEvent
import com.nhuhuy.replee.feature_auth.presentation.login.LoginScreen
import com.nhuhuy.replee.feature_auth.presentation.login.LoginViewModel
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordEvent
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordScreen
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordViewModel
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpEvent
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpScreen
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpViewModel
import com.nhuhuy.replee.feature_auth.utils.toUiText
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthDestination : NavKey{
    @Serializable
    data object Login: AuthDestination
    @Serializable
    data object SignUp: AuthDestination
    @Serializable
    data object ForgotPassword: AuthDestination
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
        val viewModel: LoginViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()
        val snackBarHostState = remember { SnackbarHostState() }
        val resource = LocalResources.current
        ObserveEffect(viewModel.event) { event ->
            when (event) {
                is LoginEvent.Failure -> {
                    snackBarHostState.showSnackbar(
                        message = resource.getString(event.error.toUiText()),
                        duration = SnackbarDuration.Short
                    )
                }

                LoginEvent.NavigateToHome -> {
                    backstack.removeAll{ destination ->
                        destination is AuthDestination
                    }
                    backstack.add(HomeDestination.ConversationList)
                }

                LoginEvent.NavigateToRecover -> {
                    backstack.add(AuthDestination.ForgotPassword)
                }

                LoginEvent.NavigateToSignUp -> {
                    backstack.add(AuthDestination.SignUp)
                }
            }
        }
        LoginScreen(
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction
        )
    }

    entry<AuthDestination.SignUp>(
        metadata = AUTH_METADATA_TRANSITION
    ) { _ ->
        val viewModel: SignUpViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()
        val snackBarHostState = remember { SnackbarHostState() }
        val resource = LocalResources.current
        ObserveEffect(viewModel.event) { event ->
            when (event) {
                is SignUpEvent.Failure -> {
                    snackBarHostState.showSnackbar(
                        message = resource.getString(event.error.toUiText()),
                        duration = SnackbarDuration.Short
                    )
                }

                SignUpEvent.NavigateBack -> backstack.removeLastOrNull()
                SignUpEvent.SignUpSuccessfully -> {
                    backstack.removeAll{ destination ->
                        destination is AuthDestination
                    }
                    backstack.add(HomeDestination.Chat)
                }
            }
        }
        SignUpScreen(
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction
        )
    }

    entry<AuthDestination.ForgotPassword>(
        metadata = AUTH_METADATA_TRANSITION
    ) { _ ->
        val viewModel: RecoverPasswordViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()
        val snackBarHostState = remember { SnackbarHostState() }
        val resource = LocalResources.current
        ObserveEffect(viewModel.event) { event ->
            when (event) {
                is RecoverPasswordEvent.Failure -> {
                    snackBarHostState.showSnackbar(
                        message = resource.getString(event.error.toUiText()),
                        duration = SnackbarDuration.Short
                    )
                }

                RecoverPasswordEvent.NavigateBack -> backstack.removeLastOrNull()
                RecoverPasswordEvent.SendEmailSuccessfully -> {
                    snackBarHostState.showSnackbar(
                        message = resource.getString(R.string.recover_screen_success_snack_bar),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
        RecoverPasswordScreen(
            state = state,
            snackBarHostState = snackBarHostState,
            onAction = viewModel::onAction
        )
    }
}
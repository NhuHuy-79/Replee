package com.nhuhuy.replee.feature_auth.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.core.design_system.component.VisibleLoadingScreen
import com.nhuhuy.replee.feature_auth.data.model.toStringRes
import com.nhuhuy.replee.feature_auth.presentation.login.LoginEvent
import com.nhuhuy.replee.feature_auth.presentation.login.LoginScreen
import com.nhuhuy.replee.feature_auth.presentation.login.LoginViewModel
import com.nhuhuy.replee.feature_auth.utils.toUiText

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: (userId: String) -> Unit,
    onNavigateToSignUp: () -> Unit,
) = BoxContainer {
    val state by viewModel.state.collectAsStateWithLifecycle()
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

            is LoginEvent.NavigateToHome -> onNavigateToHome(event.uid)

            LoginEvent.NavigateToRecover -> onNavigateToForgotPassword()

            LoginEvent.NavigateToSignUp -> onNavigateToSignUp()

            is LoginEvent.GoogleErrorSnackBar -> {
                val stringRes = event.error.toStringRes()
                if (stringRes != null) {
                    snackBarHostState.showSnackbar(
                        message = resource.getString(stringRes),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    LoginScreen(
        state = state,
        snackBarHostState = snackBarHostState,
        onAction = viewModel::onAction
    )

    VisibleLoadingScreen(
        modifier = Modifier.fillMaxSize(),
        show = state.showLoading
    )
}
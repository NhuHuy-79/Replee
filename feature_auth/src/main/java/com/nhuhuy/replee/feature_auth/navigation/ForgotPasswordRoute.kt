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
import com.nhuhuy.replee.feature_auth.R
import com.nhuhuy.replee.feature_auth.presentation.recover_password.ForgotPasswordScreen
import com.nhuhuy.replee.feature_auth.presentation.recover_password.ForgotPasswordViewModel
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordEvent
import com.nhuhuy.replee.feature_auth.utils.toUiText

@Composable
fun ForgotPasswordRoute(
    viewModel: ForgotPasswordViewModel,
    onNavigateBack: () -> Unit,
) = BoxContainer {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resource = LocalResources.current
    ObserveEffect(viewModel.event) { event ->
        when (event) {
            is RecoverPasswordEvent.SendEmailFailure -> {
                snackBarHostState.showSnackbar(
                    message = resource.getString(event.error.toUiText()),
                    duration = SnackbarDuration.Short
                )
            }

            RecoverPasswordEvent.NavigateBack -> onNavigateBack()
            RecoverPasswordEvent.SendEmailSuccessfully -> {
                snackBarHostState.showSnackbar(
                    message = resource.getString(R.string.recover_screen_success_snack_bar),
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    ForgotPasswordScreen(
        state = state,
        snackBarHostState = snackBarHostState,
        onAction = viewModel::onAction
    )

    VisibleLoadingScreen(
        modifier = Modifier.fillMaxSize(),
        show = state.showLoading
    )
}
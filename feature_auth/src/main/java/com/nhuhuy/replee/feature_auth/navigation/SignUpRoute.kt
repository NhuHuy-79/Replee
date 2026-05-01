package com.nhuhuy.replee.feature_auth.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpEvent
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpScreen
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpViewModel
import com.nhuhuy.replee.feature_auth.utils.toUiText

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: (userId: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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

            SignUpEvent.NavigateBack -> onNavigateBack()
            is SignUpEvent.NavigateToHome -> onNavigateToHome(event.uid)
        }
    }

    SignUpScreen(
        state = state,
        snackBarHostState = snackBarHostState,
        onAction = viewModel::onAction
    )
}

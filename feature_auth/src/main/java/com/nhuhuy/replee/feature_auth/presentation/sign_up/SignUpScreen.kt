package com.nhuhuy.replee.feature_auth.presentation.sign_up

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_auth.R
import com.nhuhuy.replee.feature_auth.presentation.shared.AuthButton
import com.nhuhuy.replee.feature_auth.presentation.shared.AuthLayout
import com.nhuhuy.replee.core.design_system.component.VisibleLoadingScreen
import com.nhuhuy.replee.feature_auth.presentation.shared.NormalTextField
import com.nhuhuy.replee.feature_auth.presentation.shared.SecureTextField
import com.nhuhuy.replee.feature_auth.utils.toUiText

@Composable
fun SignUpScreen(
    state: SignUpState,
    snackBarHostState: SnackbarHostState,
    onAction: (SignUpAction) -> Unit,
) {
    AuthLayout(
        titleRes = R.string.sign_up_screen_title,
        bgRes = R.drawable.bg_sign_up,
        navigationIcon = {
            IconButton(
                onClick = {
                    onAction(SignUpAction.NavigateBack)
                }
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        },
        snackBarHostState = snackBarHostState
    ) {
        item {
            NormalTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.sign_up_name,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = null
                    )
                },
                errorText = state.name.validateResult.toUiText(),
                dynamicInput = state.name,
                onValueChange = { value ->
                    onAction(SignUpAction.OnNameChange(value))
                }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            NormalTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.sign_up_email,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null
                    )
                },
                errorText = state.email.validateResult.toUiText(),
                dynamicInput = state.email,
                onValueChange = { value ->
                    onAction(SignUpAction.OnEmailChange(value))
                }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SecureTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.sign_up_password,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null
                    )
                },
                errorText = state.password.validateResult.toUiText(),
                dynamicInput = state.password,
                onValueChange = { value ->
                    onAction(SignUpAction.OnPasswordChange(value))
                }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SecureTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.sign_up_confirm_password,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null
                    )
                },
                errorText = state.confirmPassword.validateResult.toUiText(),
                dynamicInput = state.confirmPassword,
                onValueChange = { value ->
                    onAction(SignUpAction.OnConfirmPasswordChange(value))
                }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            AuthButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                res = R.string.sign_up_button,
                enabled = state.inputValid,
                onClick = {
                    onAction(SignUpAction.SignUp)
                }
            )
        }
    }

    VisibleLoadingScreen(
        modifier = Modifier.fillMaxSize(),
        show = state.showLoading
    )
}
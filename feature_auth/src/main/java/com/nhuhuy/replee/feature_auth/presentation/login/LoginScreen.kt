@file:OptIn(InternalCoroutinesApi::class)

package com.nhuhuy.replee.feature_auth.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.core.design_system.component.CommonButton
import com.nhuhuy.replee.core.design_system.component.NormalTextField
import com.nhuhuy.replee.core.design_system.component.SecureTextField
import com.nhuhuy.replee.core.design_system.component.VisibleLoadingScreen
import com.nhuhuy.replee.feature_auth.R
import com.nhuhuy.replee.feature_auth.presentation.shared.AuthLayout
import com.nhuhuy.replee.feature_auth.presentation.shared.customText
import com.nhuhuy.replee.feature_auth.utils.toUiText
import kotlinx.coroutines.InternalCoroutinesApi

@Composable
fun LoginScreen(
    state: LoginState,
    snackBarHostState: SnackbarHostState,
    onAction: (LoginAction) -> Unit
) = BoxContainer {
    AuthLayout(
        titleRes = R.string.login_screen_title,
        bgRes = R.drawable.bg_sign_in,
        snackBarHostState = snackBarHostState
    ) {
        item {
            NormalTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.login_screen_email,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null
                    )
                },
                errorText = state.email.validateResult.toUiText(),
                validatableInput = state.email,
                onValueChange = { value ->
                    onAction(LoginAction.OnEmailChanged(value))
                }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SecureTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.login_screen_password,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null
                    )
                },
                errorText = state.password.validateResult.toUiText(),
                validatableInput = state.password,
                onValueChange = { value ->
                    onAction(LoginAction.OnPasswordChanged(value))
                }
            )
        }

        item {
            Text(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAction(LoginAction.NavigateToRecover)
                    },
                textAlign = TextAlign.End
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            CommonButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                res = R.string.login_screen_button,
                enabled = state.inputValid,
                onClick = {
                    onAction(LoginAction.OnLoginWithEmail)
                }
            )
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            Text(
                text = customText(
                    first = R.string.dont_have_an_account,
                    second = R.string.login_screen_sign_up,
                    firstStyle = SpanStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    secondStyle = SpanStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable{
                    onAction(LoginAction.NavigateToSignUp)
                }
            )
        }

        item {
            Spacer(Modifier.height(12.dp))
        }

        item {
            GoogleSignInContent(
                onClick = {
                    onAction(LoginAction.OnLoginWithGoogle)
                }
            )
        }
    }

    VisibleLoadingScreen(
        modifier = Modifier.fillMaxSize(),
        show = state.showLoading
    )
}
package com.nhuhuy.replee.feature_auth.presentation.recover_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_auth.R
import com.nhuhuy.replee.core.design_system.component.CommonButton
import com.nhuhuy.replee.feature_auth.presentation.shared.AuthLayout
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.core.design_system.component.VisibleLoadingScreen
import com.nhuhuy.replee.core.design_system.component.NormalTextField
import com.nhuhuy.replee.feature_auth.utils.toUiText

@Composable
fun RecoverPasswordScreen(
    state: RecoveryPasswordState,
    snackBarHostState: SnackbarHostState,
    onAction: (RecoverPasswordAction) -> Unit
) = BoxContainer {
    AuthLayout(
        titleRes = R.string.recover_screen_title,
        bgRes = R.drawable.bg_forgot_password,
        navigationIcon = {
            IconButton(
                onClick = {
                    onAction(RecoverPasswordAction.OnBack)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        },
        snackBarHostState = snackBarHostState
    ){
        item {
            NormalTextField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.recover_screen_email,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null
                    )
                },
                errorText = state.email.validateResult.toUiText(),
                dynamicInput = state.email,
                onValueChange = { value ->
                    onAction(RecoverPasswordAction.OnEmailChange(value))
                }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            CommonButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                res = R.string.recover_screen_button,
                enabled = state.inputValid,
                onClick = {
                    onAction(RecoverPasswordAction.OnSubmit)
                }
            )
        }
    }

    VisibleLoadingScreen(
        modifier = Modifier.fillMaxSize(),
        show = state.showLoading
    )
}
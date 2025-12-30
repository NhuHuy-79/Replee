package com.nhuhuy.replee.feature_auth

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.nhuhuy.replee.feature_auth.presentation.login.LoginScreen
import com.nhuhuy.replee.feature_auth.presentation.login.LoginState
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoverPasswordScreen
import com.nhuhuy.replee.feature_auth.presentation.recover_password.RecoveryPasswordState
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpScreen
import com.nhuhuy.replee.feature_auth.presentation.sign_up.SignUpState

@Composable
@Preview
fun LoginTheme(){
    val snackBarHostState = remember { SnackbarHostState() }
    LoginScreen(
        state = LoginState(),
        snackBarHostState = snackBarHostState
    ) { }
}

@Composable
@Preview
fun SignUpTheme(){
    val snackBarHostState = remember { SnackbarHostState() }
    SignUpScreen(
        state = SignUpState(),
        snackBarHostState = snackBarHostState
    ) { }
}

@Composable
@Preview
fun RecoverPasswordTheme(){
    val snackBarHostState = remember { SnackbarHostState() }
    RecoverPasswordScreen(
        state = RecoveryPasswordState(),
        snackBarHostState = snackBarHostState
    ){

    }
}
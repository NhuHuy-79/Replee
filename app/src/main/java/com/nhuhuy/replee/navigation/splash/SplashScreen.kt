package com.nhuhuy.replee.navigation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.LocalMainUiState
import com.nhuhuy.replee.R

@Composable
fun SplashScreen(
    navigateToHome: (uid: String) -> Unit,
    navigateToLogin: () -> Unit,
) {
    val mainState = LocalMainUiState.current
    LaunchedEffect(mainState.showSplashScreen, mainState.authenticationState) {
        if (!mainState.showSplashScreen) {
            if (mainState.authenticationState != null) {
                navigateToHome(mainState.authenticationState)
            } else {
                navigateToLogin()
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.bg_splash),
            contentDescription = null,
        )

        Spacer(Modifier.height(16.dp))

        LinearProgressIndicator()

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.splash_screen),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

package com.nhuhuy.replee.feature_auth.presentation.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_auth.R

@Composable
fun GoogleSignInContent(
    loadingState: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.size(48.dp), // 👈 FIX CỨNG SIZE
        contentAlignment = Alignment.Center
    ) {

        IconButton(
            onClick = onClick,
            enabled = !loadingState
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        if (loadingState) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 4.dp
            )
        }
    }
}
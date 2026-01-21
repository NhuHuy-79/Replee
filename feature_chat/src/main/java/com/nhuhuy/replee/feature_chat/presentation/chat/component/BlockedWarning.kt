package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.R

@Preview
@Composable
fun BlockedWarning(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.errorContainer
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.banner_blocked_user),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onErrorContainer
        )

    }
}
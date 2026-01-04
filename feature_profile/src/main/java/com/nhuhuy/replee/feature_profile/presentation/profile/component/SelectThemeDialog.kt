package com.nhuhuy.replee.feature_profile.presentation.profile.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nhuhuy.replee.feature_profile.R
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode

@Composable
fun SelectThemeDialog(
    modifier: Modifier = Modifier,
    currentThemeMode: ThemeMode,
    onThemeModeSelect: (theme: ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        icon = {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(R.string.app_theme_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.app_theme_content),
                    style = MaterialTheme.typography.bodyMedium
                )
                ThemeMode.entries.forEach { themeMode ->
                    BaseRadioButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(themeMode.toStringRes()),
                        selected = themeMode == currentThemeMode,
                        onClick = {
                            onThemeModeSelect(themeMode)
                        }
                    )
                }
            }
        }
    )
}

fun ThemeMode.toStringRes() : Int {
    return when (this) {
        ThemeMode.DEFAULT -> R.string.app_theme_default
        ThemeMode.DARK -> R.string.app_theme_dark
        ThemeMode.LIGHT -> R.string.app_theme_light
    }
}
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
import androidx.compose.ui.tooling.preview.Preview
import com.nhuhuy.replee.feature_profile.R
import com.nhuhuy.replee.feature_profile.data.data_store.NotificationMode

@Composable
fun NotificationDialog(
    currentOption: NotificationMode,
    onNotificationSelect: (notification: NotificationMode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
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
                text = stringResource(R.string.notification_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.notification_dialog_content),
                    style = MaterialTheme.typography.bodyLarge
                )
                NotificationMode.entries.forEach { notificationMode ->
                    BaseRadioButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(notificationMode.toStringRes()),
                        selected = notificationMode == currentOption,
                        onClick = {
                            onNotificationSelect(notificationMode)
                        }
                    )
                }
            }
        }
    )
}

fun NotificationMode.toStringRes() : Int {
    return when (this) {
        NotificationMode.NONE -> R.string.notification_off
        NotificationMode.PRIVATE -> R.string.notification_private
        NotificationMode.GROUP -> R.string.notification_group
    }
}

@Preview
@Composable
fun PreviewDialog(modifier: Modifier = Modifier) {
    NotificationDialog(
        currentOption = NotificationMode.NONE,
        onNotificationSelect = {},
        onDismiss = {}
    )
}
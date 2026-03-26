package com.nhuhuy.replee.feature_profile.presentation.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nhuhuy.replee.feature_profile.R

@Composable
fun EditDialog(
    onDismiss: () -> Unit,
    onEditPassword: () -> Unit,
    onEditAvatar: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            shadowElevation = 12.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = null
                )

                Text(
                    text = stringResource(R.string.edit_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        onClick = {
                            onDismiss()
                            onEditPassword()

                        }
                    ) {
                        Text(
                            text = stringResource(R.string.edit_dialog_password),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        onClick = {
                            onDismiss()
                            onEditAvatar()

                        }
                    ) {
                        Text(
                            text = stringResource(R.string.edit_dialog_avatar),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDialog() {
    EditDialog(
        onDismiss = {},
        onEditPassword = {},
        onEditAvatar = {}
    )

}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.nhuhuy.replee.core.design_system.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nhuhuy.replee.core.design_system.R

@Preview
@Composable
fun ContentDialogContainer(
) {
    BasicAlertDialog(
        onDismissRequest = {}
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}

@Composable
fun AlertDialogContainer(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    @StringRes title: Int,
    @StringRes content: Int,
    icon: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(stringResource(title))
        },
        text = {
            Text(stringResource(content))
        },
        icon = icon,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    stringResource(R.string.dialog_confirm_btn)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.dialog_dismiss_btn))
            }
        }
    )
}


package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Reply
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.SheetContainer
import com.nhuhuy.replee.feature_chat.R


@Composable
fun MessageSheet(
    onDismiss: () -> Unit,
    onMessagePin: () -> Unit,
    onMessageDelete: () -> Unit,
    onMessageCopy: () -> Unit,
    onMessageReply: () -> Unit,
) {
    SheetContainer(
        onDismiss = onDismiss
    ) {
        MessageSheetOption(
            headingIcon = Icons.Rounded.ContentCopy,
            text = stringResource(R.string.message_sheet_copy),
            onClick = onMessageCopy
        )

        MessageSheetOption(
            headingIcon = Icons.AutoMirrored.Rounded.Reply,
            text = stringResource(R.string.message_sheet_reply),
            onClick = onMessageReply
        )

        MessageSheetOption(
            headingIcon = Icons.Rounded.PushPin,
            text = stringResource(R.string.message_sheet_pin),
            onClick = onMessagePin
        )

        MessageSheetOption(
            headingIcon = Icons.Rounded.Delete,
            text = stringResource(R.string.message_sheet_delete),
            onClick = onMessageDelete
        )
    }
}


@Composable
fun MessageSheetOption(
    headingIcon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = headingIcon,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}



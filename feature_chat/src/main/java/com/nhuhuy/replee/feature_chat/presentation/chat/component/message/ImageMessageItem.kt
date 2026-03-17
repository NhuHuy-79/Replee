package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.domain.model.Message


@Composable
fun SenderImageMessageItem(
    senderName: String,
    senderImgUrl: String,
    onImagePress: (urlKey: String) -> Unit,
    message: Message,
    modifier: Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        UserImage(
            userName = senderName,
            photoUrl = senderImgUrl,
            modifier = Modifier.size(48.dp)
        )

        Spacer(Modifier.width(4.dp))

        ImageMessageContainer(
            showStatus = false,
            message = message,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onClick = onImagePress
        )
    }
}

@Composable
fun ReceiverImageMessageItem(
    message: Message,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    onImagePress: (urlKey: String) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        ImageMessageContainer(
            showStatus = isLast,
            message = message,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = onImagePress
        )

    }
}
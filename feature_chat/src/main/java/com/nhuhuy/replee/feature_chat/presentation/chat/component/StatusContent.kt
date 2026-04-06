package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus

@Composable
fun StatusContent(
    otherUserReadingTime: Long,
    modifier: Modifier = Modifier,
    message: Message,
    receiverImageUrl: String,
    receiverName: String,
) {

    val messageStatus = remember(otherUserReadingTime, message.status) {
        if (otherUserReadingTime > message.sentAt) {
            MessageStatus.SEEN
        } else {
            message.status
        }
    }

    Box(
        modifier = modifier
            .size(16.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when (messageStatus) {
            MessageStatus.SYNCED -> {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            MessageStatus.PENDING -> {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            MessageStatus.FAILED -> {
                Icon(
                    imageVector = Icons.Rounded.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            MessageStatus.SEEN -> {
                UserImage(
                    userName = receiverName,
                    photoUrl = receiverImageUrl,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
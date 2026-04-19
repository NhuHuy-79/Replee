package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.image.ImageMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.text.TextMessage

@Composable
fun MessageContent(
    localPathMessage: LocalPathMessage,
    isAnchor: Boolean,
    isMine: Boolean,
    onLongClick: () -> Unit,
    onClick: (message: Message) -> Unit,
    modifier: Modifier = Modifier,
) {
    val message = localPathMessage.message
    val isReplying = message.repliedMessageId != null

    val containerColor = when {
        isAnchor -> MaterialTheme.colorScheme.tertiary
        isMine -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        isAnchor -> MaterialTheme.colorScheme.onTertiary
        isMine -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .onMessageLongPress(
                onLongClick = onLongClick,
                onClick = { onClick(message) }
            )
    ) {
        when (message.type) {
            MessageType.TEXT -> {
                TextMessage(
                    modifier = Modifier,
                    isReplying = isReplying,
                    localPathMessage = localPathMessage,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }

            MessageType.IMAGE -> {
                ImageMessage(
                    localPathMessage = localPathMessage,
                    modifier = Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                )
            }
        }
    }
}
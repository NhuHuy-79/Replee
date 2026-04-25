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
    isCurrentUser: Boolean,
    onLongClick: () -> Unit,
    onClick: (message: Message) -> Unit,
    modifier: Modifier = Modifier,
) {
    val message = localPathMessage.message
    val isReplying = message.repliedMessageId != null

    val containerColor = when {
        isAnchor -> MaterialTheme.colorScheme.tertiaryContainer
        isCurrentUser -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when {
        isAnchor -> MaterialTheme.colorScheme.onTertiaryContainer
        isCurrentUser -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .onMessageLongPress(
                onLongClick = onLongClick,
                onClick = { onClick(message) }
            )
    ) {
        when (message.type) {
            MessageType.TEXT -> {
                TextMessage(
                    modifier = modifier,
                    isReplying = isReplying,
                    localPathMessage = localPathMessage,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }

            MessageType.IMAGE -> {
                ImageMessage(
                    localPathMessage = localPathMessage,
                    modifier = modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = if (isReplying) RoundedCornerShape(
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        ) else RoundedCornerShape(16.dp)
                    )
                )
            }
        }
    }
}
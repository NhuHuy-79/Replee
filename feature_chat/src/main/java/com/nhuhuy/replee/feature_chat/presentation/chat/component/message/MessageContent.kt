package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.image.ImageMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.text.TextMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition

@Composable
fun MessageContent(
    localPathMessage: LocalPathMessage,
    position: MessagePosition,
    isAnchor: Boolean,
    isCurrentUser: Boolean,
    onLongClick: () -> Unit,
    onClick: (message: Message) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isReplying = localPathMessage.message.repliedMessageId != null
    val message = localPathMessage.message
    val containerColor = when {
        isAnchor -> MaterialTheme.colorScheme.primary
        isCurrentUser -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        isAnchor -> MaterialTheme.colorScheme.onPrimary
        isCurrentUser -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val messageTextShape = remember(position, isCurrentUser) {
        val large = 24.dp
        val small = 4.dp
        if (isCurrentUser) {
            when (position) {
                MessagePosition.START -> {
                    if (isReplying) {
                        RoundedCornerShape(
                            topStart = large,
                            topEnd = small,
                            bottomEnd = small,
                            bottomStart = large
                        )
                    } else {
                        RoundedCornerShape(
                            topStart = large,
                            topEnd = large,
                            bottomEnd = small,
                            bottomStart = large
                        )
                    }
                }

                MessagePosition.MIDDLE -> RoundedCornerShape(
                    topStart = large,
                    topEnd = small,
                    bottomEnd = small,
                    bottomStart = large
                )

                MessagePosition.END -> {
                    RoundedCornerShape(
                        topStart = large,
                        topEnd = small,
                        bottomEnd = large,
                        bottomStart = large
                    )
                }

                MessagePosition.SINGLE -> {
                    if (isReplying) {
                        RoundedCornerShape(
                            topStart = large,
                            topEnd = small,
                            bottomEnd = large,
                            bottomStart = large
                        )
                    } else {
                        RoundedCornerShape(
                            topStart = large,
                            topEnd = large,
                            bottomEnd = large,
                            bottomStart = large
                        )
                    }
                }
            }
        } else {
            when (position) {
                MessagePosition.START -> {
                    if (isReplying) {
                        RoundedCornerShape(
                            topStart = small,
                            topEnd = large,
                            bottomEnd = large,
                            bottomStart = small
                        )
                    } else RoundedCornerShape(
                        topStart = large,
                        topEnd = large,
                        bottomEnd = large,
                        bottomStart = small
                    )
                }

                MessagePosition.MIDDLE -> RoundedCornerShape(
                    topStart = small,
                    topEnd = large,
                    bottomEnd = large,
                    bottomStart = small
                )

                MessagePosition.END -> RoundedCornerShape(
                    topStart = small,
                    topEnd = large,
                    bottomEnd = large,
                    bottomStart = large
                )

                MessagePosition.SINGLE -> RoundedCornerShape(
                    topStart = large,
                    topEnd = large,
                    bottomEnd = large,
                    bottomStart = large
                )
            }
        }
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
                    modifier = modifier
                        .clip(shape = messageTextShape),
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
                        shape = RoundedCornerShape(16.dp)
                    )
                )
            }
        }
    }
}

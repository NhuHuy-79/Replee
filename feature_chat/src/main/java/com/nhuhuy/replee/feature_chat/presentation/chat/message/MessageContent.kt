package com.nhuhuy.replee.feature_chat.presentation.chat.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nhuhuy.replee.feature_chat.domain.model.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.onMessageLongPress
import com.nhuhuy.replee.feature_chat.presentation.chat.message.image.ImageMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.message.text.TextMessage

@Composable
fun MessageContent(
    localPathMessage: LocalPathMessage,
    isMine: Boolean,
    onLongClick: () -> Unit,
    onClick: (message: Message) -> Unit,
    modifier: Modifier = Modifier,
) {
    val message = localPathMessage.message
    val isReplying = message.repliedMessageId != null

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
                    isMine = isMine,
                    localPathMessage = localPathMessage,
                )
            }

            MessageType.IMAGE -> {
                ImageMessage(
                    localPathMessage = localPathMessage
                )
            }
        }
    }
}
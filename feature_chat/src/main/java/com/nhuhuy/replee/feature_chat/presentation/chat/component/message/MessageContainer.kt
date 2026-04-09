package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType

@Composable
fun MessageContainer(
    replyTo: String,
    localPathMessage: LocalPathMessage,
    isMine: Boolean,
    onClick: (message: Message) -> Unit,
    onLongClick: () -> Unit
) {
    val isReplying: Boolean = localPathMessage.message.repliedMessageId != null
    val message = localPathMessage.message

    var replyWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Column(
        modifier = Modifier,
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        if (isReplying) {
            ReplyContent(
                sender = replyTo,
                content = message.repliedMessageContent.orEmpty(),
                type = message.repliedMessageType,
                remoteUrl = message.repliedMessageRemoteUrl,
                modifier = Modifier.onSizeChanged { size ->
                    replyWidth = with(density) { size.width.toDp() }
                }
            )
        }

        MessageContent(
            localPathMessage = localPathMessage,
            isMine = isMine,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = Modifier
                .then(
                    if (replyWidth > 0.dp) Modifier.width(replyWidth)
                    else Modifier.wrapContentWidth()
                )
        )
    }
}


@Composable
fun ReplyContent(
    sender: String,
    content: String,
    type: MessageType?,
    remoteUrl: String?,
    modifier: Modifier = Modifier,
) {
    val messageContent: String = when (type) {
        MessageType.TEXT -> content
        MessageType.IMAGE -> stringResource(R.string.reply_image)
        else -> ""
    }

    Row(
        modifier = modifier
            .wrapContentWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.reply_title, sender),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = messageContent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (type == MessageType.IMAGE) {
            AsyncImage(
                model = remoteUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
                    .sizeIn(maxWidth = 60.dp, maxHeight = 60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )
        }

    }
}
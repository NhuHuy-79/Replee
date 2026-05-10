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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition.END
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition.MIDDLE
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition.SINGLE
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition.START


@Composable
fun ReplyContent(
    mainMessagePosition: MessagePosition,
    isCurrentUser: Boolean,
    replyTo: String,
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

    val shape = if (isCurrentUser) {
        when (mainMessagePosition) {
            START -> RoundedCornerShape(
                topStart = 24.dp,
                bottomStart = 24.dp,
                bottomEnd = 4.dp,
                topEnd = 24.dp
            )

            MIDDLE -> RoundedCornerShape(
                topStart = 24.dp,
                bottomStart = 24.dp,
                bottomEnd = 4.dp,
                topEnd = 4.dp
            )

            END -> RoundedCornerShape(
                topStart = 24.dp,
                bottomStart = 24.dp,
                bottomEnd = 4.dp,
                topEnd = 4.dp
            )

            SINGLE -> RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomEnd = 4.dp,
                bottomStart = 24.dp
            )
        }
    } else {
        when (mainMessagePosition) {
            START -> RoundedCornerShape(
                topStart = 24.dp,
                bottomStart = 4.dp,
                bottomEnd = 24.dp,
                topEnd = 24.dp
            )

            MIDDLE -> RoundedCornerShape(
                topStart = 4.dp,
                bottomStart = 4.dp,
                bottomEnd = 24.dp,
                topEnd = 24.dp
            )

            END -> RoundedCornerShape(
                topStart = 4.dp,
                bottomStart = 4.dp,
                bottomEnd = 24.dp,
                topEnd = 24.dp
            )

            SINGLE -> RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomEnd = 24.dp,
                bottomStart = 4.dp
            )
        }
    }

    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                text = stringResource(R.string.reply_title, replyTo),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = messageContent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
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

package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus


@Composable
fun MyMessageItem(
    isLast: Boolean,
    message: Message,
    modifier: Modifier = Modifier
) {
    var clicked by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxWidth = screenWidth * 0.7f

    @StringRes
    val stringRes = remember(message.status) {
        when (message.status) {
            MessageStatus.PENDING -> R.string.message_status_sending
            MessageStatus.SYNCED -> R.string.message_status_sent
            MessageStatus.FAILED -> R.string.message_status_failed
            MessageStatus.SEEN -> R.string.message_status_seen
        }
    }

    Column(
        modifier = modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { clicked = !clicked },
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                TextMessageContent(
                    text = message.content,
                    textColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (isLast || clicked) {
            Text(
                text = stringResource(stringRes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 10.dp)
            )
        }
    }
}
@Composable
fun OtherMessageItem(
    imgUrl: String,
    userName: String,
    message: Message,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxWidth = screenWidth * 0.7f

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        UserImage(
            userName = userName,
            photoUrl = imgUrl,
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            TextMessageContent(
                text = message.content,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
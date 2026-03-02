package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ImageMessageContainer(
    message: Message,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val maxWidth = screenWidth * 0.7f
    val shape = RoundedCornerShape(16.dp)

    val stringRes: Int? = remember(message.status) {
        when (message.status) {
            MessageStatus.FAILED -> R.string.message_status_failed
            MessageStatus.SEEN -> R.string.message_status_seen
            else -> null
        }
    }

    Column(
        modifier = modifier
    ) {

        // 👉 Container chung cho cả pending & sent
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .aspectRatio(1f)
                .clip(shape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {

            when (message.status) {

                MessageStatus.PENDING -> {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp)
                    )
                }

                MessageStatus.FAILED -> {
                    Icon(
                        imageVector = Icons.Rounded.BrokenImage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                else -> {
                    AsyncImage(
                        model = message.content,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize(),
                        error = painterResource(R.drawable.ic_broken_img)
                    )
                }
            }
        }

        stringRes?.let {
            Text(
                text = stringResource(it),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 10.dp, top = 4.dp)
            )
        }
    }
}
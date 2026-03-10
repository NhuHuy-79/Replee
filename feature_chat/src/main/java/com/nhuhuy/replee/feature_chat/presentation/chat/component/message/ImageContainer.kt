package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ImageMessageContainer(
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
    onClick: (url: String) -> Unit,
    message: Message,
    containerColor: Color,
    contentColor: Color,

) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxWidth: Dp = screenWidth * 0.7f
    val shape = RoundedCornerShape(8.dp)

    val stringRes: Int? = remember(message.status) {
        when (message.status) {
            MessageStatus.FAILED -> R.string.message_status_failed
            MessageStatus.SEEN -> R.string.message_status_seen
            MessageStatus.SYNCED -> R.string.message_status_sent
            else -> null
        }
    }

    Column(
        modifier = modifier.clickable {
            onClick(message.content)
        }
    ) {

        SubcomposeAsyncImage(
            model = message.content,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .widthIn(max = maxWidth)
                .heightIn(max = 350.dp)
                .clip(shape),

            loading = {
                LoadingStateImage(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            },

            error = {
                FailureStateImage(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
        )

        if (stringRes != null && isLast) {
            Text(
                text = stringResource(stringRes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 10.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun FailureStateImage(
    contentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(200.dp)
            .width(200.dp)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.BrokenImage,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = contentColor
        )
    }
}

@Composable
fun LoadingStateImage(
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(200.dp)
            .width(200.dp)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = contentColor
        )
    }
}
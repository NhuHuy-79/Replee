package com.nhuhuy.replee.feature_chat.presentation.chat.component.image

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ImageMessageContainer(
    modifier: Modifier = Modifier,
    localPathMessage: LocalPathMessage,
    containerColor: Color,
    contentColor: Color,
) {
    val message = localPathMessage.message
    val imageModel = localPathMessage.localPath ?: message.remoteUrl
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxWidth = screenWidth * 0.6f
    val maxHeight = 250.dp
    val isReplying = localPathMessage.message.repliedMessageId != null

    val imageModifier = remember(localPathMessage.width, localPathMessage.height) {
        if (localPathMessage.width > 0 && localPathMessage.height > 0) {
            modifier
                .widthIn(max = maxWidth)
                .aspectRatio(
                    (localPathMessage.width.toFloat() / localPathMessage.height).coerceIn(
                        0.3f,
                        1f
                    )
                )
        } else {
            modifier
                .widthIn(max = maxWidth)
                .heightIn(max = maxHeight)
        }
    }
    val shape = if (isReplying) RoundedCornerShape(
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    ) else RoundedCornerShape(16.dp)

    SubcomposeAsyncImage(
        model = imageModel,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = imageModifier
            .clip(shape),

        loading = {
            LoadingStateImage(
                containerColor = containerColor,
                contentColor = contentColor,
                modifier = Modifier.fillMaxSize()
            )
        },

        error = {
            FailureStateImage(
                containerColor = containerColor,
                contentColor = contentColor,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
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
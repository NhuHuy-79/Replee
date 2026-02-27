package com.nhuhuy.replee.feature_chat.presentation.chat.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nhuhuy.replee.feature_chat.R


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ImageMessageContent(
    modifier: Modifier = Modifier,
    imgUrl: String?,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    AsyncImage(
        model = imgUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .widthIn(max = screenWidth * 0.7f)
            .heightIn(max = 320.dp)
            .clip(RoundedCornerShape(16.dp)),
        error = painterResource(R.drawable.ic_broken_img)
    )
}

@Composable
fun TextMessageContent(
    modifier: Modifier = Modifier,
    text: String,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimary
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = textColor,
        softWrap = true,
        modifier = modifier
    )
}


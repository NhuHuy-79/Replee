package com.nhuhuy.replee.core.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun UserImage(
    modifier: Modifier = Modifier,
    userName: String,
    photoUrl: String? = null
) {
    UserAvatar(
        userName = userName,
        photoUrl = photoUrl,
        modifier = modifier,
    )
}

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    userName: String,
    photoUrl: String? = null,
) {
    val url = photoUrl?.trim().orEmpty()
    if (url.isBlank()) {
        UserAvatarFallback(
            userName = userName,
            modifier = modifier.size(56.dp)
        )
        return
    }

    val context = LocalPlatformContext.current

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "User Avatar",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape),

        loading = {
            UserAvatarFallback(
                userName = userName,
                modifier = Modifier.size(56.dp)
            )
        },
        error = {
            UserAvatarFallback(
                userName = userName,
                modifier = Modifier.size(56.dp)
            )
        }
    )
}

@Composable
private fun UserAvatarFallback(
    userName: String,
    modifier: Modifier = Modifier,
) {
    val letter = userName
        .trim()
        .takeIf { it.isNotEmpty() }
        ?.first()
        ?.uppercase()
        ?: "U"

    Box(
        modifier = modifier
            .background(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
package com.nhuhuy.replee.feature_chat.presentation.chat.message.image

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nhuhuy.replee.feature_chat.domain.model.LocalPathMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.component.image.ImageMessageContainer

@Composable
fun ImageMessage(
    modifier: Modifier = Modifier,
    localPathMessage: LocalPathMessage,
) {
    ImageMessageContainer(
        modifier = modifier,
        localPathMessage = localPathMessage,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary
    )
}
package com.nhuhuy.replee.feature_chat.presentation.chat.message.image

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.component.image.ImageMessageContainer

@Composable
fun ImageMessage(
    modifier: Modifier = Modifier,
    localPathMessage: LocalPathMessage,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        ImageMessageContainer(
            modifier = Modifier,
            localPathMessage = localPathMessage,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        )
    }
}
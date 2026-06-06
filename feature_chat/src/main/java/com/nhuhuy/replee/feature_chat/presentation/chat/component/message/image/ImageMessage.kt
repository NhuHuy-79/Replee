package com.nhuhuy.replee.feature_chat.presentation.chat.component.message.image

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.component.image.ImageMessageContainer

@Composable
fun ImageMessage(
    modifier: Modifier = Modifier,
    localPathMessage: LocalPathMessage,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(
        16.dp
    )
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        ImageMessageContainer(
            modifier = Modifier,
            localPathMessage = localPathMessage,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary,
            shape = shape
        )
    }
}

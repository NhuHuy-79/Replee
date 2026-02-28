package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun TextMessageContent(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = textColor,
        softWrap = true,
        modifier = modifier
    )
}


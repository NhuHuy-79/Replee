package com.nhuhuy.replee.feature_chat.presentation.chat.component.message.text

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhuhuy.replee.core.model.chat.LocalPathMessage

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TextMessage(
    modifier: Modifier = Modifier,
    localPathMessage: LocalPathMessage,
    containerColor: Color,
    contentColor: Color,

) {
    Row(
        modifier = modifier
            .background(color = containerColor)
            .padding(horizontal = 12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextMessageCore(
            text = localPathMessage.message.content,
            color = contentColor
        )
    }
}

@Composable
fun TextMessageCore(
    text: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium
        ),
        color = color,
        softWrap = true,
        modifier = modifier
    )
}

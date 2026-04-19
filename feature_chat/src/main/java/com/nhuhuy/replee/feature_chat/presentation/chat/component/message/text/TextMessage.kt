package com.nhuhuy.replee.feature_chat.presentation.chat.component.message.text

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TextMessage(
    modifier: Modifier = Modifier,
    isReplying: Boolean,
    localPathMessage: LocalPathMessage,
    containerColor: Color,
    contentColor: Color,

) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp * 0.75f

    Row(
        modifier = modifier
            .widthIn(max = maxWidth)
            .background(
                color = containerColor,
                shape = if (isReplying) RoundedCornerShape(
                    bottomEnd = 16.dp, bottomStart = 16.dp
                ) else RoundedCornerShape(20.dp)
            )
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
            lineHeight = 22.sp
        ),
        color = color,
        softWrap = true,
        modifier = modifier
    )
}
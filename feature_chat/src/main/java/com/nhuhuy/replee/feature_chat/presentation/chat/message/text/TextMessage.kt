package com.nhuhuy.replee.feature_chat.presentation.chat.message.text

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TextMessage(
    isReplying: Boolean,
    isMine: Boolean,
    localPathMessage: LocalPathMessage,
    modifier: Modifier = Modifier
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp * 0.75f

    Row(
        modifier = modifier
            .widthIn(max = maxWidth)
            .background(
                color = if (isMine) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = if (isReplying) RoundedCornerShape(
                    bottomEnd = 16.dp, bottomStart = 16.dp
                ) else RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextMessageCore(
            isMine = isMine,
            text = localPathMessage.message.content,
        )
    }
}

@Composable
fun TextMessageCore(
    isMine: Boolean,
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            lineHeight = 22.sp
        ),
        color = if (isMine) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        softWrap = true,
        modifier = modifier
    )
}
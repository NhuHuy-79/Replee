package com.nhuhuy.replee.feature_chat.presentation.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.common.utils.formatToChatTime
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.domain.model.message.Message

@Composable
fun SearchResultItem(
    modifier: Modifier = Modifier,
    message: Message,
    senderName: String,
    senderImgUrl: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(
            16.dp, Alignment.Start
        )
    ) {
        UserImage(
            userName = senderName,
            photoUrl = senderImgUrl,
            modifier = Modifier.size(48.dp)
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
        ) {
            Text(
                text = senderName,
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = message.sentAt.formatToChatTime(),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
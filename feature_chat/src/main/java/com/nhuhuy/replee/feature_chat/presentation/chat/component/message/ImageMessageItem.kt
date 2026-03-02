package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.domain.model.Message


@Composable
fun SenderImageMessageItem(
    senderName: String,
    senderImgUrl: String,
    message: Message,
    modifier: Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        UserImage(
            userName = senderName,
            photoUrl = senderImgUrl
        )

        Spacer(Modifier.width(4.dp))

        ImageMessageContainer(message = message)
    }
}

@Composable
fun ReceiverImageMessageItem(
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        ImageMessageContainer(message = message)

    }
}
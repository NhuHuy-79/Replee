package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.domain.model.Message

@Composable
fun MessageContainer(
    showInfo: Boolean,
    onShowInfo: (show: Boolean) -> Unit,
    isOwnerMessage: Boolean,
    message: Message,
    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier.animateContentSize()
            .wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {


        Text(
            text = if (message.seen) "Seen" else "Sent",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun MyMessageItem(
    isLast: Boolean,
    message: Message,
    modifier: Modifier = Modifier
) {
    var clicked by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier.wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { clicked = !clicked }
            ),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (isLast || clicked) {
            Text(
                text = if (message.seen) stringResource(R.string.message_status_seen) else
                    stringResource(R.string.message_status_sent),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End).padding(end = 10.dp)
            )
        }
    }
}

@Composable
fun OtherMessageItem(
    userName: String,
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        UserImage(
            userName = userName,
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
@Preview
fun MessageTheme() {

}
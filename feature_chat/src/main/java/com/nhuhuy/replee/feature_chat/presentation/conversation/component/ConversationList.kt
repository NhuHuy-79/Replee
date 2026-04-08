package com.nhuhuy.replee.feature_chat.presentation.conversation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.common.utils.formatToChatTime
import com.nhuhuy.replee.core.design_system.canvas.AvatarContainer
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.converastion.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType
import com.nhuhuy.replee.feature_chat.utils.getMainName

@Composable
fun ConversationList(
    conversationList: List<Conversation>,
    onConversationClick: (conversation: Conversation) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (conversationList.isEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.bg_empty),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.conversation_screen_empty),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(24.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                items = conversationList,
                key = { conversation -> conversation.id }
            ) { item ->
                ConversationItem(
                    conversation = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onConversationClick(item)
                        }
                        .animateItem()
                )
            }
        }
    }

}

@Composable
fun ConversationItem(
    conversation: Conversation,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(84.dp)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        AvatarContainer(
            showDot = conversation.otherUserOnline,
            colorDot = MaterialTheme.colorScheme.primary,
            sizeAvatar = 56.dp
        ) {
            UserImage(
                photoUrl = conversation.otherUserImg,
                userName = conversation.otherUserName,
            )
        }

        ConversationBody(
            isLastSender = conversation.lastSenderId == conversation.ownerUserId,
            lastMessageId = conversation.lastMessageId,
            lastDeletedMessageId = conversation.lastDeletedMessageId.orEmpty(),
            userName = conversation.otherUserNickName.ifEmpty { conversation.otherUserName },
            messageType = conversation.lastMessageType,
            messageContent = conversation.lastMessageContent,
            modifier = Modifier.weight(1f)
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(
                8.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.lastMessageTime?.formatToChatTime().orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )

                if (conversation.pinned) {
                    Icon(
                        imageVector = Icons.Rounded.PushPin,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (conversation.muted) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (conversation.unreadMessageCount > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "${conversation.unreadMessageCount}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationBody(
    isLastSender: Boolean,
    userName: String,
    messageType: MessageType,
    messageContent: String,
    lastMessageId: String,
    lastDeletedMessageId: String,
    modifier: Modifier = Modifier
) {
    val lastMessageContent: String = when (messageType) {
        MessageType.TEXT -> {
            if (isLastSender) {
                stringResource(R.string.conversation_last_message_text, messageContent)
            } else {
                messageContent
            }
        }

        MessageType.IMAGE -> {
            if (isLastSender) {
                stringResource(R.string.conversation_my_last_message_image)
            } else {
                stringResource(
                    R.string.conversation_other_last_message_image,
                    userName.getMainName()
                )
            }
        }
    }
    Column(
        modifier = modifier
    ) {
        Text(
            text = userName,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = if (lastMessageId == lastDeletedMessageId) stringResource(R.string.conversation_message_is_deleted) else lastMessageContent,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


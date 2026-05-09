package com.nhuhuy.replee.feature_chat.presentation.chat.component.message_bubble

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.common.utils.formatToChatTime
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.component.StatusContent
import com.nhuhuy.replee.feature_chat.presentation.chat.component.emote.EmoteFlowRow
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.MessageContent
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.ReplyContent
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessageUiModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageUiState

@Composable
fun MessageBubbleItem(
    isLastInScreen: Boolean,
    readingTime: Long,
    item: MessageUiModel.MessageItem,
    messageUiState: MessageUiState,
    chatBackgroundState: ChatBackgroundState,
    onReplyContentClick: () -> Unit,
    onImageMessageClick: (message: Message) -> Unit,
    onMessageLongClick: () -> Unit,
    onReactionClick: (reaction: String) -> Unit
) {
    var showStatus by remember { mutableStateOf(false) }
    val isAnchor = messageUiState.anchorMessageId == item.data.message.messageId
    val isCurrentUser = item.data.message.senderId == chatBackgroundState.currentAccount.id
    val sender =
        if (isCurrentUser) chatBackgroundState.currentAccount else chatBackgroundState.otherAccount
    val repliedUser =
        if (item.data.message.repliedMessageSenderId == chatBackgroundState.currentAccount.id) chatBackgroundState.currentAccount
        else chatBackgroundState.otherAccount
    val isHideReactions =
        item.data.message.ownerReactions.isEmpty() && item.data.message.otherUserReactions.isEmpty()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        MessageBubbleLayout(
            isLastInGroup = item.isLastInGroup,
            isLastInScreen = isLastInScreen,
            position = item.position,
            isCurrentUser = isCurrentUser,
            showReactions = !isHideReactions,
            showStatus = showStatus,
            isReplyMessage = item.data.message.repliedMessageId != null,
            userImageContent = {
                UserImage(
                    userName = sender.name,
                    photoUrl = sender.imageUrl,
                    modifier = Modifier.size(36.dp)
                )
            },
            timeStampContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.data.message.sentAt.formatToChatTime(),
                        style = MaterialTheme.typography.labelSmall
                    )

                    if (item.data.message.pinned) {
                        Icon(
                            imageVector = Icons.Rounded.PushPin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            },
            statusContent = {
                StatusContent(
                    otherUserReadingTime = readingTime,
                    message = item.data.message,
                    receiverImageUrl = chatBackgroundState.otherAccount.imageUrl,
                    receiverName = chatBackgroundState.otherAccount.name,
                )
            },
            replyContent = {
                ReplyContent(
                    isCurrentUser = isCurrentUser,
                    replyTo = repliedUser.name,
                    content = item.data.message.repliedMessageContent.orEmpty(),
                    type = item.data.message.repliedMessageType,
                    remoteUrl = item.data.message.repliedMessageRemoteUrl,
                    modifier = Modifier
                        .clickable(onClick = onReplyContentClick)
                )
            },
            mainContent = {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    MessageContent(
                        localPathMessage = item.data,
                        position = item.position,
                        isAnchor = isAnchor,
                        isCurrentUser = isCurrentUser,
                        onClick = {
                            when (item.data.message.type) {
                                MessageType.TEXT -> {
                                    showStatus = !showStatus
                                }

                                MessageType.IMAGE -> onImageMessageClick(it)
                            }
                        },
                        onLongClick = onMessageLongClick,
                        modifier = Modifier
                    )


                }
            },
            reactionContent = {
                val allReactions =
                    item.data.message.ownerReactions + item.data.message.otherUserReactions
                val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
                val horizontalPadding = if (isCurrentUser) 0.dp else 48.dp
                EmoteFlowRow(
                    onReactionClick = onReactionClick,
                    modifier = Modifier
                        .align(alignment = alignment)
                        .padding(horizontal = horizontalPadding),
                    reactions = allReactions,
                )
            }
        )
    }
}
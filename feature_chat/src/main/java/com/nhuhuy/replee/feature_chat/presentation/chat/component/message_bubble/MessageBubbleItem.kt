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
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.component.StatusContent
import com.nhuhuy.replee.feature_chat.presentation.chat.component.emote.EmoteFlowRow
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.MessageContent
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.ReplyContent
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import com.nhuhuy.replee.feature_chat.presentation.chat.state.MessagePosition

@Composable
fun MessageBubbleItem(
    modifier: Modifier = Modifier,
    isLastInGroup: Boolean,
    isLastInScreen: Boolean,
    position: MessagePosition,
    readingTime: Long,
    uiState: ChatState,
    item: LocalPathMessage,
    onReplyContentClick: () -> Unit,
    onTextMessageClick: (message: Message) -> Unit,
    onImageMessageClick: (message: Message) -> Unit,
    onMessageLongClick: () -> Unit,
    onReactionClick: (reaction: String) -> Unit
) {
    var showStatus by remember { mutableStateOf(false) }
    val isAnchor = uiState.messageAnchorId == item.message.messageId
    val isCurrentUser = item.message.senderId == uiState.currentUserId
    val sender = if (isCurrentUser) uiState.currentUser else uiState.otherUser
    val repliedUser =
        if (item.message.repliedMessageSenderId == uiState.currentUserId) uiState.currentUser
        else uiState.otherUser
    val isHideReactions =
        item.message.ownerReactions.isEmpty() && item.message.otherUserReactions.isEmpty()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        MessageBubbleLayout(
            isLastInGroup = isLastInGroup,
            isLastInScreen = isLastInScreen,
            position = position,
            isCurrentUser = isCurrentUser,
            showReactions = !isHideReactions,
            showStatus = showStatus,
            isReplyMessage = item.message.repliedMessageId != null,
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
                        text = item.message.sentAt.formatToChatTime(),
                        style = MaterialTheme.typography.labelSmall
                    )

                    if (item.message.pinned) {
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
                    message = item.message,
                    receiverImageUrl = uiState.otherUser.imageUrl,
                    receiverName = uiState.otherUser.name,
                )
            },
            replyContent = {
                ReplyContent(
                    isCurrentUser = isCurrentUser,
                    replyTo = repliedUser.name,
                    content = item.message.repliedMessageContent.orEmpty(),
                    type = item.message.repliedMessageType,
                    remoteUrl = item.message.repliedMessageRemoteUrl,
                    modifier = Modifier
                        .clickable(onClick = onReplyContentClick)
                )
            },
            mainContent = {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    MessageContent(
                        localPathMessage = item,
                        position = position,
                        isAnchor = isAnchor,
                        isCurrentUser = isCurrentUser,
                        onClick = {
                            when (item.message.type) {
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
                    item.message.ownerReactions + item.message.otherUserReactions
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
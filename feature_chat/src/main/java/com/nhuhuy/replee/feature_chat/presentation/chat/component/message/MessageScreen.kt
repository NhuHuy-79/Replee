package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.nhuhuy.replee.core.common.utils.formatToChatTime
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.core.presentation.animation.slideInVerticallyAnimation
import com.nhuhuy.replee.core.presentation.animation.slideOutVerticallyAnimation
import com.nhuhuy.replee.feature_chat.presentation.chat.component.StatusContent
import com.nhuhuy.replee.feature_chat.presentation.chat.component.TypingAnimatedIndicator
import com.nhuhuy.replee.feature_chat.presentation.chat.component.emote.EmoteFlowRow
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier,
    anchorMessageId: String?,
    anchorMessagePosition: Int = 1,
    recipientReadAt: Long,
    showTypingIndicator: Boolean,
    lazyListState: LazyListState,
    otherUserImg: String,
    otherUserName: String,
    onAction: (ChatAction) -> Unit,
    currentUserId: String,
    pagingItems: LazyPagingItems<LocalPathMessage>,
) {
    val scope = rememberCoroutineScope()
    val isAtBottom by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset == 0
        }
    }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(pagingItems.loadState, anchorMessageId) {
        if (anchorMessageId.isNullOrEmpty()) {
            isLoading = false
            return@LaunchedEffect
        }

        when (pagingItems.loadState.refresh) {
            is LoadState.Error -> isLoading = false
            LoadState.Loading -> isLoading = true
            is LoadState.NotLoading -> {
                lazyListState.scrollToItem(anchorMessagePosition)
                isLoading = false
            }
        }

    }

    LaunchedEffect(pagingItems.itemCount) {
        if (isAtBottom) {
            onAction(ChatAction.OnNewMessageTrigger)
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Bottom)
        ) {
            item {
                TypingAnimatedIndicator(
                    visible = showTypingIndicator,
                    name = otherUserName,
                    imgUrl = otherUserImg,
                    modifier = Modifier
                )
            }

            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { item -> item.message.messageId },
            ) { index ->
                val messageData = pagingItems[index] ?: return@items
                val isCurrentUser = messageData.message.senderId == currentUserId
                val isAnchor = messageData.message.messageId == anchorMessageId
                val replyTo =
                    if (messageData.message.repliedMessageSenderId == currentUserId) "You"
                    else otherUserName
                MessageLayout(
                    modifier = Modifier,
                    isCurrentUser = isCurrentUser,
                    userImage = {
                        UserImage(
                            userName = otherUserName,
                            photoUrl = otherUserImg,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    extraContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = messageData.message.sentAt.formatToChatTime(),
                                style = MaterialTheme.typography.labelSmall
                            )

                            if (messageData.message.pinned) {
                                Icon(
                                    imageVector = Icons.Rounded.PushPin,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    },
                    reactionContent = {
                        val allReactions =
                            messageData.message.ownerReactions + messageData.message.otherUserReactions
                        EmoteFlowRow(
                            onReactionClick = { reaction ->
                                onAction(
                                    ChatAction.OnMessageReactionClick(
                                        reaction = reaction,
                                        messageId = messageData.message.messageId
                                    )
                                )
                            },
                            modifier = Modifier,
                            reactions = allReactions,
                        )
                    },
                    statusContent = {
                        StatusContent(
                            otherUserReadingTime = recipientReadAt,
                            message = messageData.message,
                            receiverImageUrl = otherUserImg,
                            receiverName = otherUserName,
                        )
                    },
                    messageContent = {
                        MessageContainer(
                            isAnchor = isAnchor,
                            replyTo = replyTo,
                            messageItem = messageData,
                            isCurrentUser = isCurrentUser,
                            onClick = {
                                when (messageData.message.type) {
                                    MessageType.TEXT -> {

                                    }
                                    MessageType.IMAGE -> {
                                        onAction(
                                            ChatAction.OnImagePress(
                                                urlKey = messageData.localPath
                                                    ?: messageData.message.remoteUrl.orEmpty(),
                                            )
                                        )
                                    }
                                }
                            },
                            onLongClick = {
                                onAction(ChatAction.OnMessageLongPress(message = messageData.message))
                            }
                        )
                    }
                )
            }

        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        AnimatedVisibility(
            visible = !isAtBottom || anchorMessageId != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = slideInVerticallyAnimation(),
            exit = slideOutVerticallyAnimation()
        ) {
            FilledTonalIconButton(
                onClick = {
                    scope.launch {
                        if (!anchorMessageId.isNullOrEmpty()) {
                            onAction(ChatAction.OnScrollToBottom)
                        } else {
                            lazyListState.scrollToItem(0)

                        }
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDownward,
                    contentDescription = null
                )
            }
        }
    }
}

fun Modifier.onMessageLongPress(
    onLongClick: () -> Unit,
    onClick: () -> Unit = {}
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.combinedClickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        onClick = {
            onClick()
        },
        onLongClick = {
            onLongClick()
        }
    )
}

package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.nhuhuy.replee.core.common.utils.formatToChatTime
import com.nhuhuy.replee.core.design_system.animation.slideInVerticallyAnimation
import com.nhuhuy.replee.core.design_system.animation.slideOutVerticallyAnimation
import com.nhuhuy.replee.core.design_system.component.UserImage
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.component.StatusContent
import com.nhuhuy.replee.feature_chat.presentation.chat.component.TypingItem
import com.nhuhuy.replee.feature_chat.presentation.chat.message.MessageContainer
import com.nhuhuy.replee.feature_chat.presentation.chat.message.MessageLayout
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun MessageScreen(
    otherUserReadTime: Long,
    isOtherUserTyping: Boolean,
    lazyListState: LazyListState,
    otherUserImg: String,
    otherUserName: String,
    onAction: (ChatAction) -> Unit,
    modifier: Modifier = Modifier,
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

    LaunchedEffect(pagingItems.itemCount) {
        if (isAtBottom) {
            onAction(ChatAction.OnNewMessageTrigger)
        }
    }

    val refreshState = pagingItems.loadState.append

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                TypingItem(
                    visible = isOtherUserTyping,
                    name = otherUserName,
                    imgUrl = otherUserImg,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { item -> item.message.messageId },
            ) { index ->
                val localPathMessage = pagingItems[index] ?: return@items
                val isMine = localPathMessage.message.senderId == currentUserId
                val replyTo = if (localPathMessage.message.repliedMessageId == currentUserId) "You"
                else otherUserName
                MessageLayout(
                    isMine = isMine,
                    showTimeContent = false,
                    userImage = {
                        UserImage(
                            userName = otherUserName,
                            photoUrl = otherUserImg,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    timeContent = {
                        Text(
                            text = localPathMessage.message.sentAt.formatToChatTime(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    statusContent = {
                        StatusContent(
                            otherUserReadingTime = otherUserReadTime,
                            message = localPathMessage.message,
                            receiverImageUrl = otherUserImg,
                            receiverName = otherUserName,
                        )
                    },
                    messageContent = {
                        MessageContainer(
                            replyTo = replyTo,
                            localPathMessage = localPathMessage,
                            isMine = isMine,
                            onClick = {
                                when (localPathMessage.message.type) {
                                    MessageType.TEXT -> {

                                    }

                                    MessageType.IMAGE -> {
                                        onAction(
                                            ChatAction.OnImagePress(
                                                urlKey = localPathMessage.localPath
                                                    ?: localPathMessage.message.remoteUrl.orEmpty(),
                                            )
                                        )
                                    }
                                }
                            },
                            onLongClick = {
                                onAction(ChatAction.OnMessageLongPress(message = localPathMessage.message))
                            }
                        )
                    }
                )
            }

        }


        AnimatedVisibility(
            visible = refreshState is LoadState.Loading,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = !isAtBottom,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = slideInVerticallyAnimation(),
            exit = slideOutVerticallyAnimation()
        ) {
            FilledTonalIconButton(
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
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
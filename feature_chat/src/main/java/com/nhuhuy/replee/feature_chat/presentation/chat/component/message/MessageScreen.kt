package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.nhuhuy.replee.core.presentation.animation.slideInVerticallyAnimation
import com.nhuhuy.replee.core.presentation.animation.slideOutVerticallyAnimation
import com.nhuhuy.replee.feature_chat.presentation.chat.component.TypingAnimatedIndicator
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message_bubble.MessageBubbleItem
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import com.nhuhuy.replee.feature_chat.presentation.chat.state.MessagePosition
import com.nhuhuy.replee.feature_chat.presentation.chat.state.MessageUiModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun MessageScreen(
    uiState: ChatState,
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
    pagingItems: LazyPagingItems<MessageUiModel>,
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
            verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.Bottom)
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
                key = pagingItems.itemKey { item ->
                    when (item) {
                        is MessageUiModel.MessageItem -> item.data.message.messageId
                        is MessageUiModel.DateSeparator -> item.date
                    }
                },
                contentType = pagingItems.itemContentType { item ->
                    when (item) {
                        is MessageUiModel.MessageItem -> item.data.message.type
                        is MessageUiModel.DateSeparator -> "date"
                    }
                }
            ) { index ->
                when (val item = pagingItems[index]) {
                    is MessageUiModel.MessageItem -> {
                        val olderItem =
                            if (index < pagingItems.itemCount - 1) pagingItems.peek(index + 1) else null
                        val newerItem = if (index > 0) pagingItems.peek(index - 1) else null
                        val isFirstInGroup = when (olderItem) {
                            is MessageUiModel.DateSeparator -> true
                            is MessageUiModel.MessageItem -> {
                                val isDifferentSender =
                                    olderItem.data.message.senderId != item.data.message.senderId
                                val isFarApart =
                                    (item.data.message.sentAt - olderItem.data.message.sentAt) > 600_000L
                                isDifferentSender || isFarApart
                            }

                            null -> true
                        }

                        val isLastInGroup = when (newerItem) {
                            is MessageUiModel.DateSeparator -> true
                            is MessageUiModel.MessageItem -> {
                                val isDifferentSender =
                                    newerItem.data.message.senderId != item.data.message.senderId
                                val isFarApart =
                                    (newerItem.data.message.sentAt - item.data.message.sentAt) > 600_000L
                                isDifferentSender || isFarApart
                            }

                            null -> true
                        }
                        val position = when {
                            isFirstInGroup && isLastInGroup -> MessagePosition.SINGLE
                            isFirstInGroup -> MessagePosition.START
                            isLastInGroup -> MessagePosition.END
                            else -> MessagePosition.MIDDLE
                        }

                        val isLastInScreen = index == 0

                        MessageBubbleItem(
                            readingTime = recipientReadAt,
                            uiState = uiState,
                            item = item.data,
                            onReplyContentClick = {
                                //On Scroll to Reply Message

                            },
                            onTextMessageClick = {
                                //OnMessageClick
                            },
                            onImageMessageClick = { message ->
                                onAction(ChatAction.OnImagePress(message.remoteUrl.orEmpty()))
                            },
                            onMessageLongClick = {
                                onAction(ChatAction.OnMessageLongPress(message = item.data.message))
                            },
                            onReactionClick = { reaction ->
                                onAction(
                                    ChatAction.OnReactionDelete(
                                        reaction = reaction,
                                        messageId = item.data.message.messageId
                                    )
                                )
                            },
                            modifier = Modifier,
                            isLastInGroup = isLastInGroup,
                            isLastInScreen = isLastInScreen,
                            position = position
                        )
                    }

                    is MessageUiModel.DateSeparator -> {
                        DateHeader(date = item.date)
                    }

                    null -> {}
                }
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
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
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

@Composable
fun DateHeader(date: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
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

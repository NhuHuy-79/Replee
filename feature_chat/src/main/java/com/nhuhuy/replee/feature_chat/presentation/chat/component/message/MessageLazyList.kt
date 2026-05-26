package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.presentation.animation.slideInVerticallyAnimation
import com.nhuhuy.replee.core.presentation.animation.slideOutVerticallyAnimation
import com.nhuhuy.replee.feature_chat.presentation.chat.component.TypingAnimatedIndicator
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message_bubble.MessageBubbleItem
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessageUiModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundCombineState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.ScrollPosition
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.ScrollPosition.BOTTOM
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.ScrollPosition.TOP
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun MessageLazyList(
    modifier: Modifier = Modifier,
    messages: List<MessageUiModel>,
    messageContentState: MessageContentState,
    chatBackgroundState: ChatBackgroundState,
    chatBackgroundCombineState: ChatBackgroundCombineState,
    onBackgroundAction: (ChatBackgroundAction) -> Unit,
    onMessageAction: (MessageContentAction) -> Unit,
) {
    var requestAnchorMessagePosition by remember { mutableStateOf(messageContentState.anchorMessageId != null) }
    var trackedMessageId by remember { mutableStateOf<String?>(null) }

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val isAtBottom by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(messageContentState.jumpTrigger) {
        if (messageContentState.anchorMessageId != null) {
            requestAnchorMessagePosition = true
        }
    }

    LaunchedEffect(messageContentState.jumpTrigger, requestAnchorMessagePosition) {
        if (!requestAnchorMessagePosition || messageContentState.anchorMessageId == null) return@LaunchedEffect
        snapshotFlow { lazyListState.layoutInfo }
            .first { it.viewportSize.height > 0 && messages.isNotEmpty() }
            .let { layoutInfo ->
                val anchorMessageIndex = messages.indexOfFirst {
                    it is MessageUiModel.MessageItem && it.data.message.messageId ==
                            messageContentState.anchorMessageId
                }

                if (anchorMessageIndex != -1) {
                    val offset = -(layoutInfo.viewportSize.height * 1 / 3)

                    lazyListState.animateScrollToItem(
                        index = anchorMessageIndex,
                        scrollOffset = offset
                    )
                    requestAnchorMessagePosition = false
                }
            }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            val layoutInfo = lazyListState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val visibleItems = layoutInfo.visibleItemsInfo

            if (totalItems == 0) return@snapshotFlow ScrollPosition.MIDDLE to null

            val firstMessageItem = visibleItems.first()
            val lastMessageItem = visibleItems.last()

            val firstMessageItemKey = firstMessageItem.key as? String
            val lastMessageItemKey = lastMessageItem.key as? String

            when {
                lastMessageItem.index >= totalItems - messageContentState.thresholdTrigger -> TOP to lastMessageItemKey
                firstMessageItem.index <= messageContentState.thresholdTrigger -> {
                    BOTTOM to firstMessageItemKey
                }

                else -> ScrollPosition.MIDDLE to null
            }
        }
            .distinctUntilChanged()
            .filter { (scrollPosition, _) -> scrollPosition != ScrollPosition.MIDDLE }
            .collect { (scrollPosition, key) ->
                trackedMessageId = key
                when (scrollPosition) {
                    TOP -> onMessageAction(MessageContentAction.ScrollToTop)
                    BOTTOM -> onMessageAction(MessageContentAction.ScrollToBottom)
                    else -> Unit
                }
            }
    }

    LaunchedEffect(messages.size) {
        if (isAtBottom) {
            onBackgroundAction(ChatBackgroundAction.OnNewMessageTrigger)
            lazyListState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.Bottom)
        ) {
            item(
                key = "typing",
            ) {
                val showTyping = chatBackgroundCombineState.isUserTyping(
                    chatBackgroundState.currentAccount.id
                )
                TypingAnimatedIndicator(
                    visible = showTyping,
                    name = chatBackgroundState.otherAccount.name,
                    imgUrl = chatBackgroundState.otherAccount.imageUrl,
                    modifier = Modifier
                )
            }

            items(
                items = messages,
                key = { item ->
                    when (item) {
                        is MessageUiModel.MessageItem -> item.data.message.messageId
                        is MessageUiModel.DateSeparator -> "date_sep_${item.date}"
                    }
                },
                contentType = { item ->
                    when (item) {
                        is MessageUiModel.MessageItem -> item.data.message.type
                        is MessageUiModel.DateSeparator -> "date_separator"
                    }
                }
            ) { item ->
                when (item) {
                    is MessageUiModel.MessageItem -> {
                        MessageBubbleItem(
                            messageContentState = messageContentState,
                            chatBackgroundState = chatBackgroundState,
                            item = item,
                            readingTime = chatBackgroundCombineState.otherReadingTime,
                            isLastInScreen = messages.firstOrNull() == item,
                            onReplyContentClick = {
                                item.data.message.repliedMessageId?.let { repliedId ->
                                    onMessageAction(
                                        MessageContentAction.JumpToMessageContentId(
                                            repliedId
                                        )
                                    )
                                }
                            },
                            onImageMessageClick = { message ->
                                onMessageAction(MessageContentAction.OnImagePress(message.remoteUrl.orEmpty()))
                            },
                            onMessageLongClick = {
                                onMessageAction(
                                    MessageContentAction.OnMessageContentLongPress(
                                        message = item.data.message
                                    )
                                )
                            },
                            onReactionClick = { reaction ->
                                onMessageAction(
                                    MessageContentAction.OnReactionDelete(
                                        reaction = reaction,
                                        messageId = item.data.message.messageId
                                    )
                                )
                            },
                        )
                    }

                    is MessageUiModel.DateSeparator -> {
                        DateHeader(date = item.date)
                    }
                }
            }

        }

        AnimatedVisibility(
            visible = !isAtBottom || messageContentState.anchorMessageId != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = slideInVerticallyAnimation(),
            exit = slideOutVerticallyAnimation()
        ) {
            FilledTonalIconButton(
                onClick = {
                    scope.launch {
                        if (!messageContentState.anchorMessageId.isNullOrEmpty()) {
                            onMessageAction(MessageContentAction.JumpToBottom)
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

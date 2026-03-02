package com.nhuhuy.replee.feature_chat.presentation.chat.component.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(FlowPreview::class)
@Composable
fun MessageScreen(
    otherUserImg: String,
    otherUserName: String,
    modifier: Modifier = Modifier,
    currentUserId: String,
    pagingItems: LazyPagingItems<Message>,
    markMessagesRead: (ids: Set<String>) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val isAtBottom by remember(lazyListState) {
        derivedStateOf {
            val visible = lazyListState.layoutInfo.visibleItemsInfo
            // reverseLayout = true => item đầu tiên visible thường là newest
            val newestVisibleIndex = visible.firstOrNull()?.index ?: return@derivedStateOf true
            newestVisibleIndex == 0
        }
    }


    LaunchedEffect(lazyListState, pagingItems) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .map { visibleInfos ->
                val count = pagingItems.itemCount

                visibleInfos
                    .mapNotNull { info ->
                        val idx = info.index
                        if (idx in 0 until count) {
                            pagingItems.peek(idx)?.messageId
                        } else null
                    }
                    .toSet()
            }
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .debounce(300)
            .collect { visibleIds ->
                Timber.d("visibleIds = $visibleIds")
                markMessagesRead(visibleIds)
            }
    }

    // =========================
    // 3) Auto scroll to bottom khi có tin nhắn mới
    //    - chỉ scroll nếu user đang ở bottom
    // =========================
    val refreshState = pagingItems.loadState.refresh
    val appendState = pagingItems.loadState.append

    if (refreshState is LoadState.Loading || appendState is LoadState.Loading) {
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

    LaunchedEffect(pagingItems.itemCount, isAtBottom) {
        if (isAtBottom) {
            lazyListState.animateScrollToItem(0)
        }
    }

    // =========================
    // UI
    // =========================
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { item -> item.messageId }
            ) { index ->
                val message = pagingItems.peek(index) ?: return@items

                when (message.type) {
                    MessageType.TEXT -> {
                        if (message.senderId == currentUserId) {
                            MyMessageItem(
                                isLast = index == 0,
                                message = message
                            )
                        } else {
                            OtherMessageItem(
                                userName = otherUserName,
                                message = message,
                                imgUrl = otherUserImg
                            )
                        }
                    }

                    MessageType.IMAGE -> {
                        if (message.senderId == currentUserId) {
                            ReceiverImageMessageItem(message = message)
                        } else {
                            SenderImageMessageItem(
                                senderName = otherUserName,
                                senderImgUrl = otherUserImg,
                                message = message,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            // Error load older
            val appendError = appendState as? LoadState.Error
            if (appendError != null) {
                Timber.d("Load more failed: ${appendError.error.message}")
            }

            // End reached (hết tin nhắn cũ)
            /*if (pagingItems.loadState.append.endOfPaginationReached && pagingItems.itemCount > 0) {
                item {
                    Text(
                        text = "Đã tới đầu cuộc trò chuyện",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }*/
        }

        AnimatedVisibility(
            visible = !isAtBottom,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = fadeIn() + expandIn(expandFrom = Alignment.BottomCenter),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.BottomCenter)
        ) {
            FilledTonalIconButton(
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(
                            index = 0,
                            scrollOffset = 0
                        )
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
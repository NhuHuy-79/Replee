package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.compose.LazyPagingItems
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessageUiModel


@Composable
fun ChatScrollStateObserver(
    scrollState: ChatScrollState,
    pagingItems: LazyPagingItems<MessageUiModel>
) {
    LaunchedEffect(scrollState.lazyListState) {
        snapshotFlow { scrollState.lazyListState.layoutInfo.visibleItemsInfo.firstOrNull() }
            .collect { firstVisibleItem ->
                if (firstVisibleItem != null) {
                    val messageId = firstVisibleItem.key as? String
                    scrollState.updateTracking(
                        messageId = messageId,
                        offset = firstVisibleItem.offset
                    )
                }
            }
    }

    LaunchedEffect(pagingItems.itemCount) {
        val currentSnapshotList = pagingItems.itemSnapshotList.items
        scrollState.performScroll(currentSnapshotList)
    }
}

@Composable
fun rememberChatScrollState(
    lazyListState: LazyListState = rememberLazyListState(),
    initialAnchorId: String? = null
): ChatScrollState {
    return remember(lazyListState, initialAnchorId) {
        ChatScrollState(lazyListState, initialAnchorId)
    }
}

@Stable
class ChatScrollState(
    val lazyListState: LazyListState,
    anchorMessageId: String? = null
) {
    var trackedMessageId by mutableStateOf<String?>(null)
        private set

    var trackedOffset by mutableIntStateOf(0)
        private set

    var pendingJump by mutableStateOf(anchorMessageId)
        private set

    fun jumpToMessage(messageId: String) {
        pendingJump = messageId
        trackedMessageId = null
    }

    fun updateTracking(messageId: String?, offset: Int) {
        if (pendingJump == null && messageId != null) {
            trackedMessageId = messageId
            trackedOffset = offset
        }
    }

    suspend fun performScroll(messages: List<MessageUiModel>) {
        if (messages.isEmpty()) return

        pendingJump?.let { targetId ->
            val index: Int = messages.indexOfFirst { messageUiModel ->
                messageUiModel is MessageUiModel.MessageItem && messageUiModel.data.message.messageId == targetId
            }

            if (index >= 0) {
                lazyListState.scrollToItem(index, 200)
                pendingJump = null
                updateTracking(messageId = targetId, offset = 200)
            }

            return
        }

        trackedMessageId?.let { targetId ->
            val index: Int = messages.indexOfFirst { messageUiModel ->
                messageUiModel is MessageUiModel.MessageItem && messageUiModel.data.message.messageId == targetId
            }

            if (index >= 0) {
                lazyListState.scrollToItem(index, trackedOffset)
            }
        }
    }
}
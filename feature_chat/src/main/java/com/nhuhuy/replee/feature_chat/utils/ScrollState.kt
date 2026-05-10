package com.nhuhuy.replee.feature_chat.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessageUiModel
import timber.log.Timber

@Immutable
sealed interface TriggerPagingOffsetType {
    data class Append(val messageId: String, val offset: Int) : TriggerPagingOffsetType
    data class Prepend(val messageId: String, val offset: Int) : TriggerPagingOffsetType
}

@Immutable
data class MessageOffset(
    val messageId: String? = null,
    val index: Int = 0,
    val offset: Int = 0
)

@Stable
class ScrollState(
    anchorMessageId: String?,
    val lazyListState: LazyListState,
) {
    var keepAnchorMessageOffset by mutableStateOf(false)
        private set
    var appendState by mutableStateOf(MessageOffset())
        private set
    var prependState by mutableStateOf(MessageOffset())
        private set

    fun trackAppendState(messageId: String, offset: Int, index: Int) {
        appendState = appendState.copy(
            messageId = messageId,
            index = index,
            offset = offset
        )
    }

    fun trackPrependState(messageId: String, offset: Int, index: Int) {
        prependState = prependState.copy(
            messageId = messageId,
            index = index,
            offset = offset
        )
    }

    fun initializeOffset(index: Int, offset: Int = 500) {
        lazyListState.requestScrollToItem(index = index, scrollOffset = offset)
    }

    fun initializeOffsetInCenter(index: Int) {
        val layoutInfo = lazyListState.layoutInfo
        val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val centerViewportHeight = viewportHeight * 2 / 3

        initializeOffset(index = index, offset = centerViewportHeight)
    }

    suspend fun performScrollStateInAppend() {
        lazyListState.scrollToItem(index = appendState.index, scrollOffset = appendState.offset)
    }

    suspend fun performScrollStateInPrepend() {
        lazyListState.scrollToItem(index = prependState.index, scrollOffset = prependState.offset)
    }
}

@Composable
fun rememberScrollState(
    anchorMessageId: String?,
    lazyListState: LazyListState = rememberLazyListState()
): ScrollState {
    return remember(lazyListState, anchorMessageId) {
        ScrollState(anchorMessageId = anchorMessageId, lazyListState = lazyListState)
    }
}


//Observe Initial Loading at Refresh => Scroll to anchorMessageId with offset = 200
//Observe Loading Append, track TopPrefetchIndex  (last)  (int) and when scroll ToBottom, set null
//Observe Loading Prepend, track BottomPrefetchIndex (first) (int) and scroll ToItem, set null

@Composable
fun LoadingStateObserver(
    anchorMessageId: String? = null,
    scrollState: ScrollState,
    lazyPagingItems: LazyPagingItems<MessageUiModel>,
    onInitializeLazyList: () -> Unit = {},
) {
    var consumedAnchorId by remember { mutableStateOf<String?>(null) }

    if (anchorMessageId != null && anchorMessageId != consumedAnchorId) {

        if (lazyPagingItems.loadState.refresh !is LoadState.Loading && lazyPagingItems.itemCount > 0) {

            val indexOfAnchorMessage = lazyPagingItems.itemSnapshotList.indexOfFirst { message ->
                message is MessageUiModel.MessageItem && message.data.message.messageId == anchorMessageId
            }

            if (indexOfAnchorMessage != -1) {
                Timber.d("IMMEDIATE JUMP: Index $indexOfAnchorMessage - TotalItem: ${lazyPagingItems.itemCount}")

                SideEffect {
                    scrollState.initializeOffset(index = indexOfAnchorMessage, 200)
                    consumedAnchorId = anchorMessageId
                    onInitializeLazyList()
                }
            }
        }
    }
}
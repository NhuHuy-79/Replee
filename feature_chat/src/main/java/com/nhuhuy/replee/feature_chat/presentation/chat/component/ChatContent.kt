package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import timber.log.Timber

@OptIn(FlowPreview::class)
@Composable
fun ChatContent(
    otherUserName: String,
    modifier: Modifier = Modifier,
    currentUserId: String,
    messageList: List<Message>,
    markMessagesRead: (ids: Set<String>) -> Unit,
){
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo
                .mapNotNull { it.key as? String }
                .toSet()
        }
            .first { set -> set.isNotEmpty() }
            .let { set ->
                Timber.Forest.d("Set: $set")
                markMessagesRead(set)
            }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.mapNotNull { info ->
                info.key as? String
            }.toSet()
        }.distinctUntilChanged()
            .debounce(300)
            .collect { visibleIds ->
                Timber.Forest.d("visibleId: $visibleIds")
                markMessagesRead(visibleIds)
            }
    }

    LaunchedEffect(messageList.size) {
        lazyListState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        reverseLayout = false,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = messageList,
            key = { message -> message.messageId },
        ) { message ->
            if (message.senderId == currentUserId) {
                MyMessageItem(
                    isLast = messageList.indexOf(message) == 0,
                    message = message,
                )
            } else {
                OtherMessageItem(
                    userName = otherUserName,
                    message = message
                )
            }
        }
    }

}
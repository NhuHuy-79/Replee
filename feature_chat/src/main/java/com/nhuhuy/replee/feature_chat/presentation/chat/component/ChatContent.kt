package com.nhuhuy.replee.feature_chat.presentation.chat.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
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
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(FlowPreview::class)
@Composable
fun ChatContent(
    otherUserName: String,
    modifier: Modifier = Modifier,
    currentUserId: String,
    messageList: List<Message>,
    markMessagesRead: (ids: Set<String>) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val bottom by remember(lazyListState, messageList) {
        derivedStateOf {
            val layoutInfos = lazyListState.layoutInfo.visibleItemsInfo
            //Because reverseLayout = true, the first item is the last message
            val lastItemKey = layoutInfos.firstOrNull()?.key as? String
            //Newest Message(by Query in Room)
            val lastMessageKey = messageList.firstOrNull()?.messageId

            lastItemKey == lastMessageKey
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.mapNotNull { info ->
                info.key as? String
            }.toSet()
        }
            .filter { set -> set.isNotEmpty() }
            .distinctUntilChanged()
            .debounce(300)
            .collect { visibleIds ->
                Timber.Forest.d("visibleId: $visibleIds")
                markMessagesRead(visibleIds)
            }
    }

    LaunchedEffect(messageList.size) {
        lazyListState.animateScrollToItem(0)
    }

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

        AnimatedVisibility(
            visible = !bottom,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = fadeIn() + expandIn(
                expandFrom = Alignment.BottomCenter
            ),
            exit = fadeOut() + shrinkOut(
                shrinkTowards = Alignment.BottomCenter
            )
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
                ),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDownward,
                    contentDescription = null
                )
            }
        }
    }
}
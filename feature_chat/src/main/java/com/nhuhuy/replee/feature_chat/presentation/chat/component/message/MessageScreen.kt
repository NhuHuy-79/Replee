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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.nhuhuy.replee.feature_chat.domain.model.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(FlowPreview::class)
@Composable
fun MessageScreen(
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


    val refreshState = pagingItems.loadState.refresh
    val appendState = pagingItems.loadState.append

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
                key = pagingItems.itemKey { item -> item.message.messageId }
            ) { index ->
                val localPathMessage = pagingItems[index] ?: return@items

                when (localPathMessage.message.type) {
                    MessageType.TEXT -> {
                        if (localPathMessage.message.senderId == currentUserId) {
                            MyMessageItem(
                                isLast = index == 0,
                                message = localPathMessage.message,
                                modifier = Modifier
                            )
                        } else {
                            OtherMessageItem(
                                userName = otherUserName,
                                message = localPathMessage.message,
                                imgUrl = otherUserImg,
                                modifier = Modifier
                            )
                        }
                    }

                    MessageType.IMAGE -> {
                        if (localPathMessage.message.senderId == currentUserId) {
                            ReceiverImageMessageItem(
                                isLast = index == pagingItems.itemCount - 1,
                                localPathMessage = localPathMessage,
                                onImagePress = { url: String ->
                                    onAction(ChatAction.OnImagePress(urlKey = url))
                                },
                                modifier = Modifier
                            )
                        } else {
                            SenderImageMessageItem(
                                senderName = otherUserName,
                                senderImgUrl = otherUserImg,
                                localPathMessage = localPathMessage,
                                onImagePress = { url: String ->
                                    onAction(ChatAction.OnImagePress(urlKey = url))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
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
            enter = fadeIn() + expandIn(expandFrom = Alignment.BottomCenter),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.BottomCenter)
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
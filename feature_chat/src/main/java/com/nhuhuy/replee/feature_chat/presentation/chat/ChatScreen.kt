package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.ScreenStateHost
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.presentation.chat.component.MessageInput
import com.nhuhuy.replee.feature_chat.presentation.chat.component.MyMessageItem
import com.nhuhuy.replee.feature_chat.presentation.chat.component.OtherMessageItem
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import com.nhuhuy.replee.feature_chat.presentation.shared.Banner
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun ChatScreen(
    state: ChatState,
    messageList: ScreenState<List<Message>>,
    onAction: (ChatAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                otherUserName = state.otherUser.name,
                onBackClick = {
                    onAction(ChatAction.OnBackClick)
                },
                onSearchClick = {
                    //TODO("navigate to Search screen")
                },
                onMoreClick = {
                    onAction(ChatAction.OnMoreClick)
                },
            )
        },
        contentWindowInsets = WindowInsets(bottom = 8.dp)

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ScreenStateHost(
                modifier = Modifier.fillMaxWidth(),
                state = state.sendMessageState,
                success = {
                    Banner(
                        label = stringResource(R.string.chat_screen_success_banner),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                failure = {
                    Banner(
                        label = stringResource(R.string.chat_screen_failure_banner),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                loading = {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                },
                idle = {

                }
            )

            ScreenStateHost(
                modifier = Modifier.weight(1f),
                state = messageList,
                success = { list ->
                    ChatContent(
                        otherUser = state.otherUser,
                        currentUserId = state.currentUserId,
                        messageList = list,
                        markMessagesRead = { ids ->
                            onAction(ChatAction.OnReadMessage(ids))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                },
                failure = {
                    Column(
                        modifier = Modifier.wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.bg_retry),
                            contentDescription = null,
                        )

                        Text(
                            text = "Something went wrong! Please retry!",
                            style = MaterialTheme.typography.bodyLarge,
                        )

                    }

                },
                loading = {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            )

            MessageInput(
                value = state.messageInput,
                onValueChange = { value ->
                    onAction(ChatAction.OnMessageInputChanged(value))
                },
                onCameraClick = {
                    //TODO("camera clicked")
                },
                onImageClick = {
                    //TODO("image clicked")
                },
                onSendMessage = {
                    onAction(ChatAction.OnSendMessageClicked)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            )
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun ChatContent(
    otherUser: Account,
    modifier: Modifier = Modifier,
    currentUserId: String,
    messageList: List<Message>,
    markMessagesRead: (ids: Set<String>) -> Unit,
) = BoxContainer {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val isInBottom by remember(lazyListState) {
        derivedStateOf {
            val layoutInfos = lazyListState.layoutInfo
            val lastVisible = layoutInfos.visibleItemsInfo.lastOrNull()?.index
            val total = layoutInfos.totalItemsCount

            lastVisible != null && lastVisible == total - 1
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo
                .mapNotNull { it.key as? String }
                .toSet()
        }
            .first { set -> set.isNotEmpty() }
            .let { set ->
                Timber.d("Set: $set")
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
                Timber.d("visibleId: $visibleIds")
                markMessagesRead(visibleIds)
            }
    }

    LaunchedEffect(messageList.size) {
        lazyListState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = modifier,
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
                    userName = otherUser.name,
                    message = message
                )
            }
        }
    }

    if (!isInBottom) {
        FilledIconButton(
            onClick = {
                scope.launch {
                    lazyListState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowDownward,
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    otherUserName: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = otherUserName,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onMoreClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
        }
    )
}
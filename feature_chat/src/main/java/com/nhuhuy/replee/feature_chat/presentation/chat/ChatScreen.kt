@file:OptIn(ExperimentalLayoutApi::class)

package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhuhuy.replee.core.design_system.state.ScreenStateHost
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.presentation.chat.component.BlockOverlay
import com.nhuhuy.replee.feature_chat.presentation.chat.component.ChatContent
import com.nhuhuy.replee.feature_chat.presentation.chat.component.MessageInput
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import com.nhuhuy.replee.feature_chat.presentation.shared.Banner

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    blocked: Boolean,
    state: ChatState,
    messages: List<Message>,
    onAction: (ChatAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                enable = !blocked,
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
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
            )

            if (blocked) {
                BlockOverlay(
                    onUnBlock = {
                        onAction(ChatAction.OnUnblockUser)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
            } else {
                ChatContent(
                    otherUserImg = state.otherUser.imageUrl,
                    otherUserName = state.otherUser.name,
                    currentUserId = state.currentUserId,
                    messageList = messages,
                    markMessagesRead = { ids ->
                        onAction(ChatAction.OnReadMessage(ids))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
            }

            if (!blocked) {
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
                    onFocusChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    otherUserName: String,
    enable: Boolean = true,
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
                enabled = enable,
                onClick = onSearchClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null
                )
            }
            IconButton(
                enabled = enable,
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
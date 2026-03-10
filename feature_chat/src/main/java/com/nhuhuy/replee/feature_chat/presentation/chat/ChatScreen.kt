@file:OptIn(ExperimentalLayoutApi::class)

package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.paging.compose.LazyPagingItems
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.presentation.chat.component.BlockOverlay
import com.nhuhuy.replee.feature_chat.presentation.chat.component.dialog.FullImageDialog
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.MessageInput
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.MessageScreen
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatAction
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatDialog
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatState
import com.nhuhuy.replee.feature_chat.presentation.shared.Banner
import timber.log.Timber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    pagedMessages: LazyPagingItems<Message>,
    blocked: Boolean,
    state: ChatState,
    onAction: (ChatAction) -> Unit,
) = BoxContainer {
    val lazyListState = rememberLazyListState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onAction(ChatAction.OnImageSend(uri))
        } else {
            Timber.e("No media selected")
        }
    }

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

            if (state.isBlocked) {
                Banner(
                    label = stringResource(R.string.chat_screen_block_banner),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.fillMaxWidth()

                )
            }
            Spacer(Modifier.height(16.dp))

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
                MessageScreen(
                    lazyListState = lazyListState,
                    otherUserImg = state.otherUser.imageUrl,
                    otherUserName = state.otherUser.name,
                    currentUserId = state.currentUserId,
                    pagingItems = pagedMessages,
                    markMessagesRead = { ids ->
                        onAction(ChatAction.OnReadMessage(ids))
                    },
                    onAction = onAction,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
            }

            if (!blocked && !state.isBlocked) {
                MessageInput(
                    value = state.messageInput,
                    onValueChange = { value ->
                        onAction(ChatAction.OnMessageInputChanged(value))
                    },
                    onCameraClick = {
                        //TODO("camera clicked")
                    },
                    onImageClick = {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onSendMessage = {
                        onAction(ChatAction.OnSendMessageClicked)

                    },
                    scrollCallback = { lazyListState.scrollToItem(0) },
                    onFocusChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        when (val dialog = state.dialog) {
            is ChatDialog.FullImage -> {
                FullImageDialog(
                    url = dialog.url,
                    onDismiss = {
                        onAction(ChatAction.OnDismiss)
                    }
                )
            }

            ChatDialog.None -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    otherUserName: String,
    enable: Boolean = true,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onMoreClick: () -> Unit,

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
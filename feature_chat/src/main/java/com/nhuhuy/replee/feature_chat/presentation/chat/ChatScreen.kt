@file:OptIn(ExperimentalLayoutApi::class)

package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessageUiModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediatorState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundCombineState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input.MessageInputAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input.MessageInputState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    messages: List<MessageUiModel>,
    messageContentState: MessageContentState,
    chatBackgroundState: ChatBackgroundState,
    chatBackgroundCombineState: ChatBackgroundCombineState,
    chatMediatorState: ChatMediatorState,
    messageInputState: MessageInputState,
    onInputAction: (MessageInputAction) -> Unit,
    onBackgroundAction: (ChatBackgroundAction) -> Unit,
    onMessageAction: (MessageContentAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopComposable(
                chatBackgroundState = chatBackgroundState,
                onAction = onBackgroundAction
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
            MessageContentComposable(
                messages = messages,
                messageContentState = messageContentState,
                chatBackgroundCombineState = chatBackgroundCombineState,
                chatBackgroundState = chatBackgroundState,
                onMessageAction = onMessageAction,
                onBackgroundAction = onBackgroundAction
            )

            MessageInputComposable(
                chatMediatorState = chatMediatorState,
                chatBackgroundState = chatBackgroundState,
                chatBackgroundCombineState = chatBackgroundCombineState,
                messageInputState = messageInputState,
                onAction = onInputAction
            )
        }
    }
}

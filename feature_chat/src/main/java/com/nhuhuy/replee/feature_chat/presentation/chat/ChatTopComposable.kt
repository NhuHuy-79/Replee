package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.compose.runtime.Composable
import com.nhuhuy.replee.feature_chat.presentation.chat.component.ChatTopBar
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundState

@Composable
fun ChatTopComposable(
    chatBackgroundState: ChatBackgroundState,
    onAction: (ChatBackgroundAction) -> Unit
) {
    ChatTopBar(
        enable = !chatBackgroundState.isBlocked,
        otherUserName = chatBackgroundState.otherAccount.name,
        onBackClick = {
            onAction(ChatBackgroundAction.OnBackClick)
        },
        onSearchClick = {
            onAction(ChatBackgroundAction.OnSearchClick)
        },
        onPinClick = {
            onAction(ChatBackgroundAction.OnPinClick)
        },
        onMoreClick = {
            onAction(ChatBackgroundAction.OnMoreClick)
        },
    )
}
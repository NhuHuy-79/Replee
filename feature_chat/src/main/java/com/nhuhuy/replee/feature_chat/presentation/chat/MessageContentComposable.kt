package com.nhuhuy.replee.feature_chat.presentation.chat

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.presentation.component.Banner
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.presentation.chat.component.BlockOverlay
import com.nhuhuy.replee.feature_chat.presentation.chat.component.message.MessageLazyList
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessageUiModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundCombineState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background.ChatBackgroundState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentAction
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentState

@Composable
fun ColumnScope.MessageContentComposable(
    messages: List<MessageUiModel>,
    messageContentState: MessageContentState,
    chatBackgroundCombineState: ChatBackgroundCombineState,
    chatBackgroundState: ChatBackgroundState,
    onMessageAction: (MessageContentAction) -> Unit,
    onBackgroundAction: (ChatBackgroundAction) -> Unit
) {
    if (chatBackgroundState.isBlocked) {
        Banner(
            label = stringResource(R.string.chat_screen_block_banner),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.fillMaxWidth()

        )
    }
    Spacer(Modifier.height(16.dp))

    if (chatBackgroundCombineState.ownerIsBlock) {
        BlockOverlay(
            onUnBlock = {
                onBackgroundAction(ChatBackgroundAction.OnUnblockUser)
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
    } else {
        MessageLazyList(
            modifier = Modifier.weight(1f),
            messageContentState = messageContentState,
            messages = messages,
            chatBackgroundCombineState = chatBackgroundCombineState,
            chatBackgroundState = chatBackgroundState,
            onBackgroundAction = onBackgroundAction,
            onMessageAction = onMessageAction,
        )
    }
}
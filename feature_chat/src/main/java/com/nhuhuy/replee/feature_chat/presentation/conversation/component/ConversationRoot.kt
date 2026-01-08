package com.nhuhuy.replee.feature_chat.presentation.conversation.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nhuhuy.replee.core.design_system.component.LoadingScreen
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.ScreenStateHost
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationContent
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState
import com.nhuhuy.replee.feature_chat.presentation.shared.RetryScreen

@Composable
fun ConversationScreen(
    state: ConversationState,
    conversationsScreenState: ScreenState<List<Conversation>>,
    onAction: (ConversationAction) -> Unit,
){
    ScreenStateHost(
        modifier = Modifier.fillMaxSize(),
        state = conversationsScreenState,
        success = { conversations ->
            ConversationContent(
                converationList = conversations,
                state = state,
                onAction = onAction
            )
        },
        failure = {
            RetryScreen(
                modifier = Modifier,
                onRetry = {
                    onAction(ConversationAction.Retry)
                }
            )
        },
        loading = {
            LoadingScreen(modifier = Modifier.fillMaxSize())
        }
    )
}
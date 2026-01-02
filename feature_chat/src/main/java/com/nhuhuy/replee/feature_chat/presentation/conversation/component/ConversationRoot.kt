package com.nhuhuy.replee.feature_chat.presentation.conversation.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.core.design_system.component.LoadingScreen
import com.nhuhuy.replee.core.design_system.component.VisibleLoadingScreen
import com.nhuhuy.replee.core.design_system.state.ScreenStateHost
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationViewModel
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent
import com.nhuhuy.replee.feature_chat.presentation.shared.RetryScreen

@Composable
fun ConversationRoot(
    viewModel: ConversationViewModel,
    navigateToChatRoom: (conversationId: String) -> Unit,
){
    val conversationState by viewModel.conversationState.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val event = viewModel.event
    val onAction = viewModel::onAction


    ObserveEffect(event) { event ->
        when (event) {
            is ConversationEvent.NavigateToChatRoom -> {
                navigateToChatRoom(event.conversationId)
            }
        }
    }

    ScreenStateHost(
        modifier = Modifier.fillMaxSize(),
        state = conversationState,
        success = { conversations ->
            ConversationSuccessScreen(
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
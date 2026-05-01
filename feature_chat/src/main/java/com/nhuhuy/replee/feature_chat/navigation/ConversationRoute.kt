package com.nhuhuy.replee.feature_chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationScreen
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationViewModel
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent

@Composable
fun ConversationRoute(
    viewModel: ConversationViewModel,
    networkStatus: NetworkStatus,
    onNavigateToChatRoom: (currentUserId: String, otherUserId: String) -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val conversationListState by viewModel.conversationState.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            is ConversationEvent.NavigateToChatRoom -> {
                onNavigateToChatRoom(event.currentUserId, event.otherUserId)
            }

            ConversationEvent.GoToProfile -> {
                onNavigateToProfile()
            }

            is ConversationEvent.Error -> {
                // Handle error
            }
        }
    }

    ConversationScreen(
        networkStatus = networkStatus,
        conversationListState = conversationListState,
        state = state,
        searchHistory = searchHistory,
        onAction = viewModel::onAction
    )
}

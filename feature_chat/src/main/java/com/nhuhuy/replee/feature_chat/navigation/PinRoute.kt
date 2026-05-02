package com.nhuhuy.replee.feature_chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.core.presentation.component.BoxContainer
import com.nhuhuy.replee.core.presentation.component.CircularLoadingContent
import com.nhuhuy.replee.feature_chat.presentation.pin.PinEvent
import com.nhuhuy.replee.feature_chat.presentation.pin.PinViewModel
import com.nhuhuy.replee.feature_chat.presentation.pin.PinnedMessagesScreen

@Composable
fun PinRoute(
    viewModel: PinViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToConversation: (currentUserId: String, otherUserId: String, messageId: String) -> Unit
) {
    val pinnedMessages = viewModel.pinnedMessages.collectAsLazyPagingItems()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isLoading by remember { mutableStateOf(false) }

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            PinEvent.NavigateBack -> onNavigateBack()
            is PinEvent.NavigateToConversation -> {
                onNavigateToConversation(event.currentUserId, event.otherUserId, event.messageId)
            }
        }
    }

    LaunchedEffect(pinnedMessages.loadState) {
        isLoading = when (pinnedMessages.loadState.refresh) {
            is LoadState.Error -> false
            LoadState.Loading -> true
            is LoadState.NotLoading -> false
        }
    }

    BoxContainer {
        PinnedMessagesScreen(
            state = state,
            pinnedMessages = pinnedMessages,
            onAction = viewModel::onAction
        )

        if (isLoading) {
            CircularLoadingContent()
        }
    }
}

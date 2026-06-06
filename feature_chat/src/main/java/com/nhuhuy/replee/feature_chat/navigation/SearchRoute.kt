package com.nhuhuy.replee.feature_chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.feature_chat.presentation.search.SearchScreen
import com.nhuhuy.replee.feature_chat.presentation.search.SearchViewModel
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchEvent

@Composable
fun SearchRoute(
    otherUserId: String,
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (currentUserId: String, otherUserId: String, anchorSendAt: Long, anchorMessageId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchResults = viewModel.pagingMessages.collectAsLazyPagingItems()

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            SearchEvent.NavigateBack -> onNavigateBack()
            is SearchEvent.NavigateToMessage -> {
                onNavigateToChat(
                    event.currentUserId,
                    otherUserId,
                    event.anchorSendAt,
                    event.anchorMessageId
                )
            }
        }
    }

    SearchScreen(
        state = state,
        searchResults = searchResults,
        onAction = viewModel::onAction
    )
}

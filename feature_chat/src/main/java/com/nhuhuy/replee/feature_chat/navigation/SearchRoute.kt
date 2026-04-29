package com.nhuhuy.replee.feature_chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.feature_chat.presentation.search.SearchScreen
import com.nhuhuy.replee.feature_chat.presentation.search.SearchViewModel
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchEvent

@Composable
fun SearchRoute(
    otherUserId: String,
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMessage: (currentUserId: String, otherUserId: String, anchorSendAt: Long, anchorMessageId: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            SearchEvent.NavigateBack -> onNavigateBack()
            is SearchEvent.NavigateToMessage -> {
                onNavigateToMessage(
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
        query = query,
        searchResults = searchResults,
        onAction = viewModel::onAction
    )
}

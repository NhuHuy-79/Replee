@file:OptIn(ExperimentalMaterial3Api::class)

package com.nhuhuy.replee.feature_chat.presentation.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhuhuy.core.domain.model.SearchHistoryResult
import com.nhuhuy.replee.core.design_system.component.BoxContainer
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.ScreenStateHost
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.presentation.conversation.component.ConversationList
import com.nhuhuy.replee.feature_chat.presentation.conversation.component.ConversationSearchBar
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationAction
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationState
import com.nhuhuy.replee.feature_chat.presentation.shared.RetryScreen

@Composable
fun ConversationScreen(
    conversationListState: ScreenState<List<Conversation>>,
    state: ConversationState,
    searchHistory: List<SearchHistoryResult>,
    onAction: (ConversationAction) -> Unit
) = BoxContainer {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surface
                )
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            ConversationSearchBar(
                currentUser = state.currentUser,
                state = state.searchState,
                searchHistory = searchHistory,
                expand = state.expandSearchBar,
                input = state.searchQuery,
                onSearch = {
                    onAction(ConversationAction.OnSearch)
                },
                onValueChange = { value ->
                    onAction(ConversationAction.OnQueryChange(value))
                },
                onExpandChange = { expand ->
                    onAction(ConversationAction.OnExpandChange(expand))
                },
                onAvatarClick = { account ->
                    onAction(ConversationAction.OnAvatarClick(account = account))
                },
                goToProfile = {
                    onAction(ConversationAction.OnOwnerClick)
                },
                onSearchResultClick = { result ->
                    onAction(ConversationAction.OnSearchResultClick(result))
                }
            )

            ScreenStateHost(
                state = conversationListState,
                success = { conversationList ->
                    ConversationList(
                        conversationList = conversationList,
                        onConversationClick = { conversation ->
                            onAction(ConversationAction.OnConversationClick(conversation))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                },
                failure = {
                    RetryScreen(
                        modifier = Modifier.fillMaxSize(),
                        onRetry = {
                            onAction(ConversationAction.Retry)
                        }
                    )
                },
                loading = {
                    ConversationLoadingContent(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )
        }
    }
}


@Composable
private fun ConversationLoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

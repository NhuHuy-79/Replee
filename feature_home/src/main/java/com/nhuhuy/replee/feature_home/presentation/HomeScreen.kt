@file:OptIn(ExperimentalMaterial3Api::class)

package com.nhuhuy.replee.feature_home.presentation

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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import com.nhuhuy.replee.core.common.base.ScreenState
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.SearchHistoryResult
import com.nhuhuy.replee.core.presentation.ScreenStateHost
import com.nhuhuy.replee.feature_home.R
import com.nhuhuy.replee.feature_home.presentation.component.ConversationList
import com.nhuhuy.replee.feature_home.presentation.component.ConversationSearchBar
import com.nhuhuy.replee.feature_home.presentation.component.NotificationPermissionHandler
import com.nhuhuy.replee.feature_home.presentation.component.RetryScreen
import com.nhuhuy.replee.feature_home.presentation.state.HomeAction
import com.nhuhuy.replee.feature_home.presentation.state.HomeState

@Composable
fun HomeScreen(
    conversationListState: ScreenState<List<Conversation>>,
    state: HomeState,
    searchHistory: List<SearchHistoryResult>,
    onAction: (HomeAction) -> Unit
) {
    val snackBarHost = remember { SnackbarHostState() }
    val localResource = LocalResources.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { snackBarHost }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {

            NotificationPermissionHandler(
                onDeny = {
                    snackBarHost.showSnackbar(
                        message = localResource.getString(R.string.notification_permission_denied),
                        actionLabel = null
                    )
                },
                onAllow = {
                    snackBarHost.showSnackbar(
                        message = localResource.getString(R.string.notification_permission_allowed),
                        actionLabel = null
                    )
                }
            )

            ConversationSearchBar(
                currentUser = state.currentUser,
                state = state.searchState,
                searchHistory = searchHistory,
                expand = state.expandSearchBar,
                input = state.searchQuery,
                onSearch = {
                    onAction(HomeAction.OnSearch)
                },
                onValueChange = { value ->
                    onAction(HomeAction.OnQueryChange(value))
                },
                onExpandChange = { expand ->
                    onAction(HomeAction.OnExpandChange(expand))
                },
                onAvatarClick = { account ->
                    onAction(HomeAction.OnAvatarClick(account = account))
                },
                goToProfile = {
                    onAction(HomeAction.OnOwnerClick)
                },
                onSearchResultClick = { result ->
                    onAction(HomeAction.OnSearchResultClick(result))
                }
            )

            ScreenStateHost(
                state = conversationListState,
                success = { conversationList ->
                    ConversationList(
                        conversationList = conversationList,
                        onConversationClick = { conversation ->
                            onAction(HomeAction.OnHomeClick(conversation))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                },
                failure = {
                    RetryScreen(
                        modifier = Modifier.fillMaxSize(),
                        onRetry = {
                            onAction(HomeAction.Retry)
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

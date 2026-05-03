package com.nhuhuy.replee.feature_home.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.core.presentation.component.Banner
import com.nhuhuy.replee.core.presentation.component.BoxContainer
import com.nhuhuy.replee.feature_home.R
import com.nhuhuy.replee.feature_home.presentation.HomeScreen
import com.nhuhuy.replee.feature_home.presentation.HomeViewModel
import com.nhuhuy.replee.feature_home.presentation.state.HomeEvent

@Composable
fun ConversationRoute(
    viewModel: HomeViewModel,
    networkStatus: NetworkStatus,
    onNavigateToChatRoom: (currentUserId: String, otherUserId: String) -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val conversationListState by viewModel.conversationState.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            is HomeEvent.NavigateToChatRoom -> {
                onNavigateToChatRoom(event.currentUserId, event.otherUserId)
            }

            HomeEvent.GoToProfile -> {
                onNavigateToProfile()
            }

            is HomeEvent.Error -> {
                // Handle error
            }
        }
    }

    BoxContainer {
        HomeScreen(
            conversationListState = conversationListState,
            state = state,
            searchHistory = searchHistory,
            onAction = viewModel::onAction
        )

        AnimatedVisibility(
            visible = networkStatus is NetworkStatus.Offline,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Banner(
                label = stringResource(R.string.network_failed),
                modifier = Modifier.fillMaxWidth(),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

package com.nhuhuy.replee.feature_chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.feature_chat.presentation.pin.PinEvent
import com.nhuhuy.replee.feature_chat.presentation.pin.PinViewModel
import com.nhuhuy.replee.feature_chat.presentation.pin.PinnedMessagesScreen

@Composable
fun PinRoute(
    viewModel: PinViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToConversation: (currentUserId: String, otherUserId: String, messageId: String) -> Unit
) {
    val pinnedMessages by viewModel.pinnedMessage.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            PinEvent.NavigateBack -> onNavigateBack()
            is PinEvent.NavigateToConversation -> {
                onNavigateToConversation(event.currentUserId, event.otherUserId, event.messageId)
            }
        }
    }

    PinnedMessagesScreen(
        state = state,
        pinnedMessages = pinnedMessages,
        onAction = viewModel::onAction
    )
}

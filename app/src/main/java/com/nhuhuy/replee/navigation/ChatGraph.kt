package com.nhuhuy.replee.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatScreen
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatViewModel
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationViewModel
import com.nhuhuy.replee.feature_chat.presentation.conversation.component.ConversationScreen
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeDestination : NavKey {
    @Serializable
    data object ConversationList : HomeDestination

    @Serializable
    data class Chat(
        val conversationId: String,
        val ownerId: String,
        val otherUserId: String,
    ) : HomeDestination
}

fun EntryProviderScope<NavKey>.chatGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<HomeDestination.ConversationList> {
        val viewModel: ConversationViewModel = hiltViewModel()
        val conversationState by viewModel.conversationState.collectAsStateWithLifecycle()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val event = viewModel.event
        val onAction = viewModel::onAction

        ObserveEffect(event) { event ->
            when (event) {
                is ConversationEvent.NavigateToChatRoom -> {
                    backstack.add(
                        HomeDestination.Chat(
                            conversationId = event.conversationId,
                            ownerId = event.currentUserId,
                            otherUserId = event.otherUserId
                        )
                    )
                }

                ConversationEvent.GoToProfile -> {
                    backstack.add(ProfileDestination.Profile)
                }

                is ConversationEvent.Error -> {

                }
            }
        }

        ConversationScreen(
            state = state,
            conversationsScreenState = conversationState,
            onAction = onAction
        )
    }

    entry<HomeDestination.Chat> { screen ->
        val viewModel: ChatViewModel = hiltViewModel(
            key = screen.conversationId,
            creationCallback = { factory: ChatViewModel.Factory ->
                factory.create(
                    conversationId = screen.conversationId,
                    currentUserId = screen.ownerId,
                    otherUserId = screen.otherUserId
                )
            }
        )

        val state by viewModel.state.collectAsStateWithLifecycle()
        val message by viewModel.messages.collectAsStateWithLifecycle()

        ObserveEffect(viewModel.event) { event ->
            when (event) {
                ChatEvent.NavigateBack -> backstack.removeLastOrNull()
                ChatEvent.NavigateToInformation -> {
                    //TODO("navigate to information")
                }
            }
        }

        ChatScreen(
            state = state,
            messageList = message,
            onAction = viewModel::onAction
        )
    }
}

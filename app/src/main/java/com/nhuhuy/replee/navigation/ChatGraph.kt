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
import com.nhuhuy.replee.feature_chat.presentation.information.InformationScreen
import com.nhuhuy.replee.feature_chat.presentation.information.InformationViewModel
import com.nhuhuy.replee.feature_chat.presentation.information.state.InformationEvent
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

    @Serializable
    data class Information(
        val otherUserName: String,
        val otherUserId: String,
        val otherUserEmail: String
    ) : HomeDestination
}

fun EntryProviderScope<NavKey>.chatGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<HomeDestination.ConversationList> {
        val viewModel: ConversationViewModel = hiltViewModel()
        val conversationList by viewModel.conversationState.collectAsStateWithLifecycle()
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
            conversationList = conversationList,
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
        val message by viewModel.messageList.collectAsStateWithLifecycle()

        ObserveEffect(viewModel.event) { event ->
            when (event) {
                ChatEvent.NavigateBack -> backstack.removeLastOrNull()
                is ChatEvent.NavigateToInformation -> {
                    backstack.add(
                        HomeDestination.Information(
                            otherUserId = event.otherUserId,
                            otherUserName = event.otherUserName,
                            otherUserEmail = event.otherUserEmail
                        )
                    )
                }
            }
        }

        ChatScreen(
            state = state,
            messages = message,
            onAction = viewModel::onAction
        )
    }

    entry<HomeDestination.Information> { screen ->
        val viewModel: InformationViewModel = hiltViewModel(
            creationCallback = { factory: InformationViewModel.Factory ->
                factory.create(
                    otherUserId = screen.otherUserId,
                    otherUserName = screen.otherUserName,
                    otherUserEmail = screen.otherUserEmail
                )
            }
        )

        val state by viewModel.state.collectAsStateWithLifecycle()

        ObserveEffect(viewModel.event) { event ->
            when (event) {
                InformationEvent.NavigateBack -> backstack.removeLastOrNull()
            }
        }

        InformationScreen(
            state = state,
            onAction = viewModel::onAction
        )
    }
}

package com.nhuhuy.replee.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.collectAsLazyPagingItems
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatScreen
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatViewModel
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationScreen
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationViewModel
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent
import com.nhuhuy.replee.feature_chat.presentation.option.OptionScreen
import com.nhuhuy.replee.feature_chat.presentation.option.OptionViewModel
import com.nhuhuy.replee.feature_chat.presentation.option.state.OptionEvent
import com.nhuhuy.replee.navigation.HomeDestination.Information
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeDestination : NavKey {
    @Serializable
    data class ConversationList(val currentUserId: String) : HomeDestination

    @Serializable
    data class Chat(
        val ownerId: String,
        val otherUserId: String,
    ) : HomeDestination

    @Serializable
    data class Information(
        val otherUserImg: String,
        val currentUserId: String,
        val conversationId: String,
        val otherUserName: String,
        val otherUserId: String,
        val otherUserEmail: String
    ) : HomeDestination
}

fun EntryProviderScope<NavKey>.chatGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<HomeDestination.ConversationList> { screen ->
        val viewModel: ConversationViewModel = hiltViewModel(
            creationCallback = { factory: ConversationViewModel.Factory ->
                factory.create(
                    currentUserId = screen.currentUserId
                )
            }
        )
        val conversationList by viewModel.conversationState.collectAsStateWithLifecycle()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
        val event = viewModel.event
        val onAction = viewModel::onAction

        ObserveEffect(event) { event ->
            when (event) {
                is ConversationEvent.NavigateToChatRoom -> {
                    backstack.add(
                        HomeDestination.Chat(
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
            conversationListState = conversationList,
            state = state,
            searchHistory = searchHistory,
            onAction = onAction
        )
    }

    entry<HomeDestination.Chat> { screen ->
        val viewModel: ChatViewModel = hiltViewModel(
            creationCallback = { factory: ChatViewModel.Factory ->
                factory.create(
                    currentUserId = screen.ownerId,
                    otherUserId = screen.otherUserId
                )
            }
        )
        val blocked by viewModel.blocked.collectAsStateWithLifecycle()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val messagePagingState = viewModel.pagedMessages.collectAsLazyPagingItems()

        ObserveEffect(viewModel.event) { event ->
            when (event) {
                ChatEvent.NavigateBack -> backstack.removeLastOrNull()
                is ChatEvent.NavigateToInformation -> {
                    backstack.add(
                        Information(
                            otherUserId = event.otherUserId,
                            otherUserName = event.otherUserName,
                            otherUserEmail = event.otherUserEmail,
                            conversationId = event.conversationId,
                            currentUserId = event.currentUserId,
                            otherUserImg = event.otherUserImg
                        )
                    )
                }

                ChatEvent.SendImage.Failure -> {
                    //toast success
                }

                ChatEvent.SendImage.Success -> {
                    //toast failed
                }
            }
        }

        ChatScreen(
            blocked = blocked,
            state = state,
            pagedMessages = messagePagingState,
            onAction = viewModel::onAction
        )
    }

    entry<Information> { screen ->
        val viewModel: OptionViewModel = hiltViewModel(
            creationCallback = { factory: OptionViewModel.Factory ->
                factory.create(
                    otherUserId = screen.otherUserId,
                    otherUserName = screen.otherUserName,
                    otherUserEmail = screen.otherUserEmail,
                    currentUserId = screen.currentUserId,
                    conversationId = screen.conversationId,
                    otherUserImg = screen.otherUserImg
                )
            }
        )

        val state by viewModel.state.collectAsStateWithLifecycle()
        val conversation by viewModel.conversation.collectAsStateWithLifecycle()

        ObserveEffect(viewModel.event) { event ->
            when (event) {
                OptionEvent.NavigateBack -> backstack.removeLastOrNull()
                OptionEvent.NavigateToConversation -> {
                    //TODO("navigate to conversation")
                }
            }
        }

        OptionScreen(
            conversation = conversation,
            state = state,
            onAction = viewModel::onAction
        )
    }
}
package com.nhuhuy.replee.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.collectAsLazyPagingItems
import com.nhuhuy.replee.LocalNetworkStatus
import com.nhuhuy.replee.core.common.utils.showShortToast
import com.nhuhuy.replee.core.design_system.ObserveEffect
import com.nhuhuy.replee.feature_chat.R
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatScreen
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatViewModel
import com.nhuhuy.replee.feature_chat.presentation.chat.state.ChatEvent
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationScreen
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationViewModel
import com.nhuhuy.replee.feature_chat.presentation.conversation.state.ConversationEvent
import com.nhuhuy.replee.feature_chat.presentation.option.OptionScreen
import com.nhuhuy.replee.feature_chat.presentation.option.OptionViewModel
import com.nhuhuy.replee.feature_chat.presentation.option.state.OptionEvent
import com.nhuhuy.replee.feature_chat.presentation.pin.PinEvent
import com.nhuhuy.replee.feature_chat.presentation.pin.PinViewModel
import com.nhuhuy.replee.feature_chat.presentation.pin.PinnedMessagesScreen
import com.nhuhuy.replee.feature_chat.presentation.search.SearchScreen
import com.nhuhuy.replee.feature_chat.presentation.search.SearchViewModel
import com.nhuhuy.replee.feature_chat.presentation.search.state.SearchEvent
import com.nhuhuy.replee.navigation.HomeDestination.Information
import com.nhuhuy.replee.navigation.HomeDestination.Pin
import com.nhuhuy.replee.navigation.HomeDestination.Search
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

    @Serializable
    data class Search(
        val conversationId: String,
        val otherUserId: String
    ) : HomeDestination

    @Serializable
    data class Pin(
        val conversationId: String,
        val otherUserId: String
    ) : HomeDestination
}

fun EntryProviderScope<NavKey>.chatGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<HomeDestination.ConversationList> { screen ->
        val localNetworkStatus = LocalNetworkStatus.current
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
            networkStatus = localNetworkStatus,
            conversationListState = conversationList,
            state = state,
            searchHistory = searchHistory,
            onAction = onAction
        )
    }

    entry<HomeDestination.Chat> { screen ->
        val context = LocalContext.current
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
        val typingUserIds by viewModel.typingUserIds.collectAsStateWithLifecycle()
        val readingTime by viewModel.otherLastReadingTime.collectAsStateWithLifecycle()
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

                ChatEvent.FileTooLarge -> {
                    context.showShortToast(
                        message = R.string.file_too_large
                    )
                }

                ChatEvent.UnSupportedFile -> {
                    context.showShortToast(
                        message = R.string.file_unsupported
                    )
                }

                ChatEvent.Unknown -> {
                    context.showShortToast(
                        message = R.string.file_unknown
                    )
                }

                is ChatEvent.NavigateToSearch -> {
                    backstack.add(
                        Search(
                            conversationId = event.conversationId,
                            otherUserId = event.otherUserId
                        )
                    )
                }

                is ChatEvent.NavigateToPin -> {
                    backstack.add(
                        Pin(
                            conversationId = event.conversationId,
                            otherUserId = event.otherUserId
                        )
                    )
                }
            }
        }

        ChatScreen(
            otherUserReadTime = readingTime,
            typingUsers = typingUserIds,
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
        val color by viewModel.themeColor.collectAsStateWithLifecycle()
        ObserveEffect(viewModel.event) { event ->
            when (event) {
                OptionEvent.NavigateBack -> backstack.removeLastOrNull()
                OptionEvent.NavigateToConversation -> {
                    //TODO("navigate to conversation")
                }
            }
        }

        OptionScreen(
            color = color,
            state = state,
            onAction = viewModel::onAction
        )
    }

    entry<Search> { screen ->
        val searchViewModel: SearchViewModel = hiltViewModel(
            creationCallback = { factory: SearchViewModel.Factory ->
                factory.create(
                    conversationId = screen.conversationId,
                    otherUserId = screen.otherUserId
                )
            }
        )
        ObserveEffect(searchViewModel.event) { event ->
            when (event) {
                SearchEvent.NavigateBack -> backstack.removeLastOrNull()
            }
        }

        val state by searchViewModel.state.collectAsStateWithLifecycle()
        val query by searchViewModel.query.collectAsStateWithLifecycle()
        val searchResults by searchViewModel.searchResults.collectAsStateWithLifecycle()
        val onAction = searchViewModel::onAction

        SearchScreen(
            state = state,
            query = query,
            searchResults = searchResults,
            onAction = onAction
        )
    }

    entry<Pin> {
        val viewModel: PinViewModel = hiltViewModel(
            creationCallback = { factory: PinViewModel.Factory ->
                factory.create(
                    conversationId = it.conversationId,
                    otherUserId = it.otherUserId
                )
            }
        )
        val pinnedMessages by viewModel.pinnedMessage.collectAsStateWithLifecycle()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val onAction = viewModel::onAction
        ObserveEffect(viewModel.event) { event ->
            when (event) {
                PinEvent.NavigateBack -> backstack.removeLastOrNull()
            }
        }

        PinnedMessagesScreen(
            state = state,
            pinnedMessages = pinnedMessages,
            onAction = onAction
        )

    }
}
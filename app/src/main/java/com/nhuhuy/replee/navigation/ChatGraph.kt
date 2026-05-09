package com.nhuhuy.replee.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.LocalNetworkStatus
import com.nhuhuy.replee.core.common.utils.ChatIdGenerator
import com.nhuhuy.replee.feature_chat.navigation.ChatRoute
import com.nhuhuy.replee.feature_chat.navigation.OptionRoute
import com.nhuhuy.replee.feature_chat.navigation.PinRoute
import com.nhuhuy.replee.feature_chat.navigation.SearchRoute
import com.nhuhuy.replee.feature_chat.presentation.chat.ChatViewModel
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content.MessageContentViewModel
import com.nhuhuy.replee.feature_chat.presentation.option.OptionViewModel
import com.nhuhuy.replee.feature_chat.presentation.pin.PinViewModel
import com.nhuhuy.replee.feature_chat.presentation.search.SearchViewModel
import com.nhuhuy.replee.feature_home.navigation.ConversationRoute
import com.nhuhuy.replee.feature_home.presentation.HomeViewModel
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
        val currentUserId: String,
        val otherUserId: String,
        val anchorSendAt: Long? = null,
        val anchorMessageId: String? = null
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
        val currentUserId: String,
        val conversationId: String,
        val otherUserId: String
    ) : HomeDestination

    @Serializable
    data class Pin(
        val conversationId: String,
        val otherUserId: String,
        val currentUserId: String,
    ) : HomeDestination
}

fun EntryProviderScope<NavKey>.chatGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<HomeDestination.ConversationList> { screen ->
        val networkStatus = LocalNetworkStatus.current
        val viewModel: HomeViewModel = hiltViewModel(
            creationCallback = { factory: HomeViewModel.Factory ->
                factory.create(
                    currentUserId = screen.currentUserId
                )
            }
        )

        ConversationRoute(
            viewModel = viewModel,
            networkStatus = networkStatus,
            onNavigateToChatRoom = { currentUserId, otherUserId ->
                backstack.add(
                    HomeDestination.Chat(
                        currentUserId = currentUserId,
                        otherUserId = otherUserId
                    )
                )
            },
            onNavigateToProfile = {
                backstack.add(ProfileDestination.Profile)
            }
        )
    }

    entry<HomeDestination.Chat> { screen ->
        val viewModel: ChatViewModel = hiltViewModel(
            creationCallback = { factory: ChatViewModel.Factory ->
                factory.create(
                    currentUserId = screen.currentUserId,
                    otherUserId = screen.otherUserId,
                    anchorMessageId = screen.anchorMessageId
                )
            }
        )


        ChatRoute(
            messageContentViewModel = hiltViewModel(
                creationCallback = { factory: MessageContentViewModel.Factory ->
                    factory.create(
                        conversationId = ChatIdGenerator.generate(
                            uid1 = screen.currentUserId,
                            uid2 = screen.otherUserId,

                            ),
                        anchorMessageId = screen.anchorMessageId

                    )
                }
            ),
            chatViewModel = viewModel,
            onNavigateBack = { backstack.removeLastOrNull() },
            onNavigateToSearch = { conversationId, otherUserId, currentUserId ->
                backstack.add(
                    Search(
                        conversationId = conversationId,
                        otherUserId = otherUserId,
                        currentUserId = currentUserId,
                    )
                )
            },
            onNavigateToPin = { conversationId, otherUserId, currentUserId ->
                backstack.add(
                    Pin(
                        conversationId = conversationId,
                        otherUserId = otherUserId,
                        currentUserId = currentUserId
                    )
                )
            },
            onNavigateToInformation = { img, currentId, convId, otherId, name, email ->
                backstack.add(
                    Information(
                        otherUserId = otherId,
                        otherUserName = name,
                        otherUserEmail = email,
                        conversationId = convId,
                        currentUserId = currentId,
                        otherUserImg = img
                    )
                )
            }
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

        OptionRoute(
            viewModel = viewModel,
            onNavigateBack = { backstack.removeLastOrNull() },
            onNavigateToConversation = {
                backstack.removeIf { navKey ->
                    navKey is HomeDestination.Chat || navKey is Information
                }
            }
        )
    }

    entry<Search> { screen ->
        val searchViewModel: SearchViewModel = hiltViewModel(
            creationCallback = { factory: SearchViewModel.Factory ->
                factory.create(
                    conversationId = screen.conversationId,
                    otherUserId = screen.otherUserId,
                    currentUserId = screen.currentUserId
                )
            }
        )

        SearchRoute(
            otherUserId = screen.otherUserId,
            viewModel = searchViewModel,
            onNavigateBack = {
                backstack.removeLastOrNull()
            },
            onNavigateToChat = { currentUserId, otherUserId, anchorSendAt, anchorMessageId ->
                backstack.removeIf { key -> key is HomeDestination.Chat }
                backstack.add(
                    HomeDestination.Chat(
                        currentUserId = currentUserId,
                        otherUserId = otherUserId,
                        anchorSendAt = anchorSendAt,
                        anchorMessageId = anchorMessageId
                    )
                )
                backstack.removeIf { key -> key is Search }
            }
        )
    }

    entry<Pin> { screen ->
        val viewModel: PinViewModel = hiltViewModel(
            creationCallback = { factory: PinViewModel.Factory ->
                factory.create(
                    conversationId = screen.conversationId,
                    otherUserId = screen.otherUserId,
                    currentUserId = screen.currentUserId
                )
            }
        )

        PinRoute(
            viewModel = viewModel,
            onNavigateBack = { backstack.removeLastOrNull() },
            onNavigateToConversation = { currentUserId, otherUserId, messageId ->
                backstack.removeIf { key -> key is HomeDestination.Chat }
                backstack.add(
                    HomeDestination.Chat(
                        currentUserId = currentUserId,
                        otherUserId = otherUserId,
                        anchorMessageId = messageId
                    )
                )
                backstack.removeIf { key -> key is Pin }
            }
        )
    }
}

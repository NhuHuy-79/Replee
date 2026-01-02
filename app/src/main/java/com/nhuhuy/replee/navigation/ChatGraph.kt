package com.nhuhuy.replee.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.feature_chat.presentation.conversation.ConversationViewModel
import com.nhuhuy.replee.feature_chat.presentation.conversation.component.ConversationRoot
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeDestination : NavKey {
    @Serializable
    data object ConversationList : HomeDestination
    @Serializable
    data object Chat : HomeDestination
}

fun EntryProviderScope<NavKey>.chatGraph(
    backstack: NavBackStack<NavKey>,
) {
    entry<HomeDestination.ConversationList>{
        val viewModel: ConversationViewModel = hiltViewModel()
        ConversationRoot(
            viewModel = viewModel,
            navigateToChatRoom = {
                backstack.add(HomeDestination.Chat)
            }
        )
    }

    entry<HomeDestination.Chat>{

    }
}

package com.nhuhuy.replee.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
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

    }

    entry<HomeDestination.Chat>{

    }
}

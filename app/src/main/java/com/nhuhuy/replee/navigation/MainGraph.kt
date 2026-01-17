package com.nhuhuy.replee.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

private const val DURATION = 350

@Composable
fun MainGraph(
    isLogged: Boolean
){
    val startDestination: NavKey = if (isLogged) HomeDestination.ConversationList else AuthDestination.Login

    val backStack = remember(isLogged) {
        NavBackStack(startDestination)
    }
    NavDisplay(
        backStack = backStack,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(DURATION)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(DURATION)
            )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(DURATION)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(DURATION)
            )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(DURATION)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(DURATION)
            )
        },
        onBack = {
            backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()

        ),
        entryProvider = entryProvider {
            authGraph(backStack)
            chatGraph(backStack)
            profileGraph(backStack)
        }
    )
}
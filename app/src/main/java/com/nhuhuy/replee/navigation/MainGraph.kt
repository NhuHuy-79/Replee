package com.nhuhuy.replee.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nhuhuy.replee.core.network.data_source.AuthenticatedState
import com.nhuhuy.replee.navigation.splash.SplashKey
import com.nhuhuy.replee.navigation.splash.splashGraph
import kotlinx.coroutines.delay

private const val DURATION = 350

@Composable
fun MainGraph(
    authenticatedState: AuthenticatedState,
){
    val startDestination: NavKey = SplashKey
    val backStack = rememberNavBackStack(startDestination)

    LaunchedEffect(authenticatedState) {
        when (authenticatedState) {
            is AuthenticatedState.Authenticated -> {
                delay(800)
                backStack.clear()
                backStack.add(HomeDestination.ConversationList(currentUserId = authenticatedState.uid))
            }

            AuthenticatedState.Loading -> {
                backStack.add(SplashKey)
            }

            AuthenticatedState.Unauthenticated -> {
                delay(800)
                backStack.clear()
                backStack.add(AuthDestination.Login)
            }
        }
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
            splashGraph()
            authGraph(backStack)
            chatGraph(
                backstack = backStack
            )
            profileGraph(backStack)
        }
    )
}
package com.nhuhuy.replee.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.deeplink.DeepLinkDispatcher

private const val DURATION = 350

@Composable
fun MainGraph(
    isLogged: Boolean,
    startDestination: NavKey,
    deepLinkDispatcher: DeepLinkDispatcher,
    scopeHolder: ScopeHolder
) {

    val backStack = rememberNavBackStack(startDestination)

    ObserveEffect(deepLinkDispatcher.uriData) { uri ->
        deepLinkDispatcher.dispatchEvent(
            isLogged = isLogged,
            uri = uri,
            currentList = backStack.toList()
        )
    }

    ObserveEffect(deepLinkDispatcher.event) { navKeys ->
        if (navKeys.isNotEmpty() && navKeys != backStack.toList()) {
            backStack.clear()
            backStack.addAll(navKeys)
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
            authGraph(backStack)
            chatGraph(
                backstack = backStack,
                scopeHolder = scopeHolder
            )
            profileGraph(backStack)
        }
    )
}

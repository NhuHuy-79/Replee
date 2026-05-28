package com.nhuhuy.replee.deeplink

import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.navigation.AuthDestination
import com.nhuhuy.replee.navigation.HomeDestination
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

// ✅ BÂY GIỜ: Constructor chỉ chứa các cấu hình Tĩnh (Static config)
class DeepLinkDispatcher(
    private val fallbackDestination: NavKey = AuthDestination.Login,
    private val matchers: List<DeepLinkMatcher> = listOf(ChatDeepLinkMatcher)
) {
    private val _uriData = Channel<String?>()
    val uriData = _uriData.receiveAsFlow()

    private val _event = MutableSharedFlow<List<NavKey>>()
    val event = _event.asSharedFlow()

    fun submitIntent(uri: String) {
        _uriData.trySend(uri)
    }

    fun dispatchEvent(
        uri: String?,
        isLogged: Boolean,
        currentList: List<NavKey>
    ) {
        val destination = dispatch(
            uri = uri,
            isLogged = isLogged,
            currentList = currentList
        )
        _event.tryEmit(destination)
    }

    fun dispatch(
        uri: String?,
        isLogged: Boolean,
        currentList: List<NavKey>
    ): List<NavKey> {

        val destination = parse(uri)
        if (destination == fallbackDestination || !isLogged) {
            return listOf(fallbackDestination)
        }

        return when (destination) {
            is HomeDestination.Chat -> {
                val hasHome = currentList.any { it is HomeDestination.ConversationList }

                if (hasHome) {
                    currentList + destination
                } else {
                    listOf(
                        HomeDestination.ConversationList(currentUserId = destination.currentUserId),
                        destination
                    )
                }
            }

            else -> currentList + destination
        }
    }

    private fun parse(uri: String?): NavKey {
        if (uri.isNullOrBlank()) return fallbackDestination

        for (match in matchers) {
            match.match(uri)?.let { key ->
                return key
            }
        }
        return fallbackDestination
    }
}
package com.nhuhuy.replee.deeplink

import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.navigation.AuthDestination
import com.nhuhuy.replee.navigation.HomeDestination
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class DeepLinkDispatcher(
    private val fallbackDestination: NavKey = AuthDestination.Login,
    private val matchers: List<DeepLinkMatcher> = listOf(ChatDeepLinkMatcher)
) {
    private val _uriData = MutableStateFlow<String?>(null)
    val uriData = _uriData.asStateFlow()

    private val _event = MutableSharedFlow<DeepLinkResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event = _event.asSharedFlow()

    fun submitIntent(uri: String) {
        Timber.tag("DeepLinkDispatcher").d("submitIntent: $uri")
        _uriData.update { uri }
    }

    fun clearIntent() {
        _uriData.update { null }
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
    ): DeepLinkResult {

        val destination = parse(uri)
        if (destination == fallbackDestination || !isLogged) {
            return DeepLinkResult.Fallback(fallbackDestination)
        }

        return when (destination) {
            is HomeDestination.Chat -> {
                val hasHome = currentList.any { it is HomeDestination.ConversationList }
                if (hasHome) {
                    DeepLinkResult.Success(destination = destination)
                } else {
                    val backstack = listOf(
                        HomeDestination.ConversationList(currentUserId = destination.currentUserId),
                        destination
                    )
                    DeepLinkResult.NeedSyntheticBackStack(backstack = backstack)
                }
            }

            else -> DeepLinkResult.Success(destination = destination)
        }
    }

    fun release() {
        _uriData.update { null }
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

sealed interface DeepLinkResult {
    data class Fallback(val navKey: NavKey) : DeepLinkResult
    data class Success(
        val destination: NavKey
    ) : DeepLinkResult

    data class NeedSyntheticBackStack(val backstack: List<NavKey>) : DeepLinkResult
}
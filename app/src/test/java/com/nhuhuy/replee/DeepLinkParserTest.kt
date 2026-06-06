package com.nhuhuy.replee

import com.google.common.truth.Truth.assertThat
import com.nhuhuy.replee.deeplink.ChatDeepLinkMatcher
import com.nhuhuy.replee.deeplink.DOMAIN_URI
import com.nhuhuy.replee.deeplink.DeepLinkDispatcher
import com.nhuhuy.replee.deeplink.DeepLinkResult
import com.nhuhuy.replee.navigation.HomeDestination
import org.junit.Before
import org.junit.Test

class DeepLinkParserTest {
    private lateinit var deepLinkDispatcher: DeepLinkDispatcher

    @Before
    fun init() {
        deepLinkDispatcher = DeepLinkDispatcher()
    }

    @Test
    fun `parse chat uri to Chat destination correctly`() {
        val chatUri = "${DOMAIN_URI}/chat/123/456"
        val actual = ChatDeepLinkMatcher.match(chatUri)
        assertThat(actual).isEqualTo(
            HomeDestination.Chat(
                currentUserId = "123",
                otherUserId = "456"
            )
        )
    }

    @Test
    fun `dispatch chat uri when not logged in should return Fallback`() {
        val chatUri = "${DOMAIN_URI}/chat/123/456"
        val actual = deepLinkDispatcher.dispatch(
            uri = chatUri,
            isLogged = false,
            currentList = emptyList()
        )
        assertThat(actual).isInstanceOf(DeepLinkResult.Fallback::class.java)
    }

    @Test
    fun `dispatch chat uri when logged in and home exists should return Success`() {
        val chatUri = "${DOMAIN_URI}/chat/123/456"
        val actual = deepLinkDispatcher.dispatch(
            uri = chatUri,
            isLogged = true,
            currentList = listOf(HomeDestination.ConversationList("123"))
        )
        val expected = DeepLinkResult.Success(
            destination = HomeDestination.Chat(currentUserId = "123", otherUserId = "456")
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `dispatch chat uri when logged in and home NOT exists should return NeedSyntheticBackStack`() {
        val chatUri = "${DOMAIN_URI}/chat/123/456"
        val actual = deepLinkDispatcher.dispatch(
            uri = chatUri,
            isLogged = true,
            currentList = emptyList()
        )
        val expected = DeepLinkResult.NeedSyntheticBackStack(
            backstack = listOf(
                HomeDestination.ConversationList(currentUserId = "123"),
                HomeDestination.Chat(currentUserId = "123", otherUserId = "456")
            )
        )
        assertThat(actual).isEqualTo(expected)
    }
}

package com.nhuhuy.replee

import com.google.common.truth.Truth
import com.nhuhuy.replee.deeplink.ChatDeepLinkMatcher
import com.nhuhuy.replee.deeplink.DOMAIN_URI
import com.nhuhuy.replee.deeplink.DeepLinkDispatcher
import com.nhuhuy.replee.navigation.HomeDestination
import org.junit.Before
import org.junit.Test

class DeepLinkParserTest {
    private lateinit var deepLinkDispatcher: DeepLinkDispatcher

    @Before
    fun init() {
        deepLinkDispatcher = DeepLinkDispatcher(
            isLogged = true,
            currentList = listOf(HomeDestination.ConversationList("123"))
        )
    }

    @Test
    fun parseToConversationList() {
        val chatUri = "${DOMAIN_URI}/chat/123/456"
        val actual = deepLinkDispatcher.dispatch(chatUri)
        val expected = listOf(
            HomeDestination.ConversationList(currentUserId = "123"),
            HomeDestination.Chat(currentUserId = "123", otherUserId = "456")
        )
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun detectDestination() {
        val chatUri = "${DOMAIN_URI}/chat/123/456"
        val actual = ChatDeepLinkMatcher.match(chatUri)
        Truth.assertThat(actual).isEqualTo(
            HomeDestination.Chat(
                currentUserId = "123",
                otherUserId = "456"
            )
        )
    }
}
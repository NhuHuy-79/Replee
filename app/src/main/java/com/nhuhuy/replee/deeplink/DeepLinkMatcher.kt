package com.nhuhuy.replee.deeplink

import androidx.navigation3.runtime.NavKey
import com.nhuhuy.replee.navigation.HomeDestination

const val DOMAIN_URI: String = "https://replee.app"
val escapedDomain = Regex.escape(DOMAIN_URI)

interface DeepLinkMatcher {
    fun match(uri: String?): NavKey?
}

object ChatDeepLinkMatcher : DeepLinkMatcher {
    val chatDeepLinkRegex = Regex("^$escapedDomain/chat/([^/]+)/([^/]+)/?$")
    override fun match(uri: String?): NavKey? {
        if (uri == null) return null

        return chatDeepLinkRegex.find(uri)?.let { result ->
            val currentUserId = result.groupValues[1]
            val otherUserId = result.groupValues[2]
            HomeDestination.Chat(
                currentUserId = currentUserId,
                otherUserId = otherUserId
            )
        }
    }
}

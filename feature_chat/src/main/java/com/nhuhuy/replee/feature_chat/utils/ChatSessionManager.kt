package com.nhuhuy.replee.feature_chat.utils

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.Volatile

@Singleton
class ChatSessionManager @Inject constructor() {
    @Volatile
    var currentChatId: String? = null
        private set

    fun setCurrentChatId(conversationId: String?) {
        currentChatId = conversationId
    }
}
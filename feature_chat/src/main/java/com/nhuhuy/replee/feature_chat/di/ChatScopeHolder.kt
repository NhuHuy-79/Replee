package com.nhuhuy.replee.feature_chat.di

import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.ChatMediator
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatScopeHolder @Inject constructor() {
    private val mediatorHolder: ConcurrentMap<String, ChatMediator> = ConcurrentHashMap()

    fun getOrCreateChatMediator(scopeId: String): ChatMediator {
        val chatMediator = mediatorHolder[scopeId]
        return chatMediator ?: ChatMediator().also {
            mediatorHolder[scopeId] = it
        }
    }

    fun release(scopeId: String) {
        mediatorHolder.remove(scopeId)
    }
}
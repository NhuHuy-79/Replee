package com.nhuhuy.replee.feature_chat.data.data_store

interface ChatPreferences {
    suspend fun getConversationSyncFirst(): Boolean
    suspend fun getMessagesSyncFirst(): Boolean
    suspend fun updateConversationSyncFirst(value: Boolean)
    suspend fun updateMessageSyncFirst(value: Boolean)
}


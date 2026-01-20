package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor

interface ConversationSettingRepository {
    suspend fun updateSeedColor(seedColor: SeedColor)
    suspend fun muteOtherUser(conversationId: String)
    suspend fun pinConversation(conversationId: String)
    suspend fun blockOtherUser(conversationId: String)
    suspend fun deleteConversation(conversationId: String)
}
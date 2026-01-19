package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor

interface ConversationSettingRepository {
    fun updateSeedColor(seedColor: SeedColor)
    fun muteOtherUser(otherUserId: String, conversationId: String)
    fun pinConversation(conversationId: String)
    fun blockOtherUser(otherUserId: String, conversationId: String)
    fun deleteConversation(conversationId: String)
}
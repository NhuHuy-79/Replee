package com.nhuhuy.replee.feature_chat.domain.repository

import kotlinx.coroutines.flow.Flow

interface MetaDataRepository {
    suspend fun updateMyTyping(conversationId: String, userId: String, typing: Boolean)
    fun getOtherTyping(conversationId: String): Flow<List<String>>
}
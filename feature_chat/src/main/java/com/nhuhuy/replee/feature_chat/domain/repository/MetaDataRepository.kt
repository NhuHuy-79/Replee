package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import kotlinx.coroutines.flow.Flow

interface MetaDataRepository {
    suspend fun updateMyTyping(
        conversationId: String,
        userId: String,
        typing: Boolean
    ): NetworkResult<Unit>
    fun getOtherTyping(conversationId: String): Flow<List<String>>

    suspend fun updateMyReading(
        conversationId: String,
        userId: String,
        reading: Long
    ): NetworkResult<Unit>

    fun getOtherReading(conversationId: String, otherUserId: String): Flow<Long>
}
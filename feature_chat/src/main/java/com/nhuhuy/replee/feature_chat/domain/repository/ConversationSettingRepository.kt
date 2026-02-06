package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor

interface ConversationSettingRepository {
    suspend fun updateSeedColor(seedColor: SeedColor)
    suspend fun updateOwnerNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): NetworkResult<Unit>

    suspend fun updateOtherUserNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): NetworkResult<Unit>
    suspend fun muteOtherUser(
        conversationId: String,
        otherUser: String,
        muted: Boolean
    ): NetworkResult<Unit>

    suspend fun pinConversation(
        conversationId: String,
        currentUser: String,
        pinned: Boolean
    ): NetworkResult<Unit>

    suspend fun blockOtherUser(
        conversationId: String,
        otherUser: String,
        blocked: Boolean
    ): NetworkResult<Unit>

    suspend fun deleteConversation(conversationId: String): NetworkResult<Unit>
}
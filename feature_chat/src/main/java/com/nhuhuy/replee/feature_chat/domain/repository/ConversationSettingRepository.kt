package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult

interface ConversationSettingRepository {
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

    suspend fun deleteConversation(conversationId: String): NetworkResult<Unit>
}
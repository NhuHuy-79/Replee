package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.model.SeedColor
import kotlinx.coroutines.flow.Flow

interface OptionRepository {
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

    fun observeChatColor(): Flow<SeedColor>
    suspend fun selectColor(color: SeedColor)
}

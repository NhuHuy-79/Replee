package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor

interface ConversationSettingRepository {
    suspend fun updateSeedColor(seedColor: SeedColor)
    suspend fun updateOwnerNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): Resource<Unit, RemoteFailure>

    suspend fun updateOtherUserNickname(
        uid: String,
        conversationId: String,
        nickName: String
    ): Resource<Unit, RemoteFailure>
    suspend fun muteOtherUser(
        conversationId: String,
        otherUser: String,
        muted: Boolean
    ): Resource<Unit, RemoteFailure>

    suspend fun pinConversation(
        conversationId: String,
        currentUser: String,
        pinned: Boolean
    ): Resource<Unit, RemoteFailure>

    suspend fun blockOtherUser(
        conversationId: String,
        otherUser: String,
        blocked: Boolean
    ): Resource<Unit, RemoteFailure>

    suspend fun deleteConversation(conversationId: String): Resource<Unit, RemoteFailure>
}
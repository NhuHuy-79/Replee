package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import kotlinx.coroutines.flow.Flow

interface ConversationQueryRepository {
    fun listenConversationWithLimit(
        limit: Int,
        ownerId: String
    ): Flow<Unit>

    suspend fun fetchOtherUserInConversations(ownerId: String)
    suspend fun fetchConversations(): NetworkResult<List<Conversation>>
    fun observeLocalConversationList(ownerId: String): Flow<List<Conversation>>
    suspend fun getConversationById(conversationId: String): Conversation
    fun observeOtherUserInConversation(currentUserId: String): Flow<List<String>>
}

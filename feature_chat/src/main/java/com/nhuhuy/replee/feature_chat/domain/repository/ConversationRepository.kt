package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import kotlinx.coroutines.flow.Flow


interface ConversationRepository {
    fun observeConversationList() : Flow<List<Conversation>>
    suspend fun getOrCreateConversation(otherUser: Account) : Resource<String, RemoteFailure>
    suspend fun fetchConversations() : Resource<Unit, RemoteFailure>
}

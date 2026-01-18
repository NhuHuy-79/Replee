package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import kotlinx.coroutines.flow.Flow


interface ConversationRepository {
    suspend fun fetchConversations() : Resource<List<Conversation>, RemoteFailure>
    suspend fun getConversationCount() : Int
    fun observeLocalConversations() : Flow<List<Conversation>>
    suspend fun saveConversations(conversations: List<Conversation>)
    fun observeNetworkConversations() : Flow<Resource<List<Conversation>, RemoteFailure>>
    suspend fun getOrCreateConversation(otherUser: Account) : Resource<String, RemoteFailure>
}

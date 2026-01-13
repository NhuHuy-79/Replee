package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun observeMessageList(conversationId: String): Flow<Resource<List<Message>, RemoteFailure>>
    suspend fun fetchMessages(conversationId: String) : Resource<Unit, RemoteFailure>
    fun observeConversationMessages(conversationId: String) : Flow<List<Message>>
    suspend fun addNewMessage(message: Message, conversationId: String) : Resource<Message, RemoteFailure>
    suspend fun markMessageAsRead(messageIds: List<String>, conversationId: String, receiverId: String) : Resource<Unit, RemoteFailure>
}

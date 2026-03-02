package com.nhuhuy.replee.feature_chat.domain.repository

import androidx.paging.PagingData
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(message: Message): NetworkResult<String>
    suspend fun sendImage(rawMessage: Message, uriPath: String): NetworkResult<String>
    fun observeNetworkMessageList(conversationId: String): Flow<NetworkResult<List<Message>>>
    fun observeLocalMessages(conversationId: String) : Flow<List<Message>>
    suspend fun markMessagesAsRead(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String
    ): NetworkResult<Unit>
    suspend fun saveMessages(messages: List<Message>)
    suspend fun searchMessageWithQuery(conversationId: String, query: String) : List<Message>
    fun observeNetworkMessageChange(conversationId: String): Flow<List<DataChange<Message>>>
    suspend fun updateLocalDataChange(
        upsert: List<Message>,
        delete: List<String>
    )

    suspend fun fetchMessageWithPaging(
        conversationId: String,
        limit: Int = 3,
        startAfterKey: Long? = null
    ): NetworkResult<List<Message>>

    fun observeMessageChangeWithPaging(
        conversationId: String,
        limit: Int = 3
    ): Flow<List<DataChange<Message>>>

    fun observeMessageWithPaging(conversationId: String): Flow<PagingData<Message>>
}

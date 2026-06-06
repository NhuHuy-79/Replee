package com.nhuhuy.replee.core.sync.domain

import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult

interface MessageSyncRepository {
    suspend fun deleteMessages(messages: List<Message>): NetworkResult<Unit>
    suspend fun pinMessages(messages: List<Message>, pinned: Boolean): NetworkResult<Unit>
    suspend fun sendMessages(messages: List<Message>): NetworkResult<Unit>
    suspend fun reactToMessages(messages: List<Message>): NetworkResult<Unit>
}


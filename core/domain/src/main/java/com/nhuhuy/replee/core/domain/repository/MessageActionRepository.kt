package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.model.error_handling.NetworkResult

interface MessageActionRepository {
    // --- CREATE / SEND ---
    suspend fun sendMessage(message: Message): NetworkResult<String>
    suspend fun saveMessage(message: Message): String

    // --- UPDATE ---
    suspend fun updatePinStatusMessage(
        conversationId: String,
        messageId: String,
        pinned: Boolean
    ): NetworkResult<String>

    suspend fun updateRemoteUrlMessage(
        messageId: String,
        remoteUrl: String,
        status: MessageStatus
    ): Message?

    suspend fun addReaction(
        conversationId: String,
        messageId: String,
        userId: String,
        reaction: String
    ): NetworkResult<Unit>

    suspend fun removeReaction(
        conversationId: String,
        messageId: String,
        userId: String,
        reaction: String
    ): NetworkResult<Unit>

    // --- DELETE ---
    suspend fun deleteMessage(message: Message): NetworkResult<String>

    // --- SYNC / NETWORK ---
    suspend fun fetchMessagesByTimestamp(
        conversationId: String,
        timestamp: Long
    ): NetworkResult<Unit>
}

package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction

interface ActionRepository {
    suspend fun upsertAction(action: MessageAction)
    suspend fun getActionListWithType(action: MessageAction): List<MessageAction>
    suspend fun markActionAsSynced(action: MessageAction)
    suspend fun getUnSyncedActions(action: MessageAction): List<MessageAction>
    suspend fun getDeletedActions(): List<MessageAction>
}


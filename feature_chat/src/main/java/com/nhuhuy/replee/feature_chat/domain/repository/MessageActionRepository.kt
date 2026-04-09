package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction

interface MessageActionRepository {
    suspend fun upsertAction(action: MessageAction)
    suspend fun getActionListWithType(type: ActionType): List<MessageAction>
    suspend fun markActionAsSynced(action: MessageAction)
    suspend fun getUnSyncedActions(): List<MessageAction>
    suspend fun deleteMessageActionListById(actionIds: List<Long>)
}


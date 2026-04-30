package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.domain.model.message.ChatAction

interface ChatActionRepository {
    suspend fun upsertAction(action: ChatAction)
    suspend fun getActionListWithType(type: ActionType): List<ChatAction>
    suspend fun markActionAsSynced(action: ChatAction)
    suspend fun getUnSyncedActions(): List<ChatAction>
    suspend fun deleteMessageActionListById(actionIds: List<Long>)
}


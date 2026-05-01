package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.ActionType
import com.nhuhuy.replee.core.model.ChatAction

interface ChatActionRepository {
    suspend fun upsertAction(action: ChatAction)
    suspend fun getActionListWithType(type: ActionType): List<ChatAction>
    suspend fun markActionAsSynced(action: ChatAction)
    suspend fun getUnSyncedActions(): List<ChatAction>
    suspend fun deleteMessageActionListById(actionIds: List<Long>)
}


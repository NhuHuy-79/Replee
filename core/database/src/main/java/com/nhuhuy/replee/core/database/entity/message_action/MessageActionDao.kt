package com.nhuhuy.replee.core.database.entity.message_action

import androidx.room.Dao
import androidx.room.Query
import com.nhuhuy.replee.core.database.base.BaseDao

@Dao
interface MessageActionDao : BaseDao<MessageActionEntity> {

    @Query("SELECT * FROM message_modifier WHERE actionType = :type AND synced = 0")
    suspend fun getMessageActionListByType(type: String): List<MessageActionEntity>

    @Query("UPDATE message_modifier SET synced = 1 WHERE actionType = :type")
    suspend fun markActionAsSyncedByType(type: String)

    @Query("DELETE FROM message_modifier WHERE synced = 1")
    suspend fun deleteAllSyncedActions()
}
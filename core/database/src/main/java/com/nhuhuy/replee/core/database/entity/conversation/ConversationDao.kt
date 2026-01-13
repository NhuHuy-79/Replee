package com.nhuhuy.replee.core.database.entity.conversation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nhuhuy.replee.core.database.base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao : BaseDao<ConversationEntity> {

    @Query("SELECT * FROM conversation WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationEntity?


    @Transaction
    @Query("SELECT * FROM conversation WHERE ownerId = :ownerId ORDER BY lastMessageTime DESC")
    fun observeConversations(ownerId: String): Flow<List<ConversationAndUser>>


    @Transaction
    @Query("SELECT * FROM conversation WHERE id = :id")
    suspend fun getConversationAndUserById(id: String): ConversationAndUser?

}
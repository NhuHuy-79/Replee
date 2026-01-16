package com.nhuhuy.replee.feature_chat.data.source.conversation

import com.nhuhuy.replee.core.database.entity.conversation.ConversationAndUser
import com.nhuhuy.replee.core.database.entity.conversation.ConversationDao
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversationLocalDataSource @Inject constructor(
    private val conversationDao: ConversationDao
) {

    suspend fun addConversationList(entities: List<ConversationEntity>){
        conversationDao.upsertAll(entities)
    }

    suspend fun getOrCreateConversation(
        ownerId: String,
        otherUserId: String
    ): ConversationAndUser {
        val conversationId = generateConversationId(ownerId, otherUserId)

        conversationDao.getConversationById(conversationId)
            ?: conversationDao.upsert(
                ConversationEntity(
                    id = conversationId,
                    ownerId = ownerId,
                    otherUserId = otherUserId,
                )
            )

        return conversationDao.getConversationAndUserById(conversationId)
            ?: throw IllegalStateException(
                "Conversation exists but related user not found. User table not synced yet."
            )
    }

    fun observeConversationAndUsers(uid: String) : Flow<List<ConversationAndUser>>{
        return conversationDao.observeConversations(uid)
    }

    suspend fun updateSyncedTime(conversationIds: List<String>, lastMessageTime: Long) {
        conversationDao.updateSyncedTime(conversationIds, lastMessageTime)
    }

    private fun generateConversationId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString(separator = "_")
    }

}
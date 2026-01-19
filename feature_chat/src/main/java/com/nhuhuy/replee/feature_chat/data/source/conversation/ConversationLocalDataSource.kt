package com.nhuhuy.replee.feature_chat.data.source.conversation

import com.nhuhuy.replee.core.database.entity.conversation.ConversationAndUser
import com.nhuhuy.replee.core.database.entity.conversation.ConversationDao
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversationLocalDataSource @Inject constructor(
    private val conversationDao: ConversationDao
) {
    suspend fun upsertConversations(entities: List<ConversationEntity>){
        conversationDao.upsertAll(entities)
    }



    suspend fun getConversationAndUserById(
        ownerId: String,
        otherUserId: String
    ): ConversationAndUser {
        val conversationId = createConversationId(ownerId, otherUserId)

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

    suspend fun updateLastSyncedTime(conversationIds: List<String>, lastMessageTime: Long) {
        conversationDao.updateSyncedTime(conversationIds, lastMessageTime)
    }

    private fun createConversationId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString(separator = "_")
    }

    suspend fun getConversationsCount(ownerId: String): Int{
        return conversationDao.getConversationListCount(ownerId)
    }

    suspend fun updateLastMessage(message: MessageEntity){
        conversationDao.updateLastMessage(
            conversationId = message.conversationId,
            lastMessageTime = message.sentAt ?: -1L,
            lastMessageContent = message.content,
            lastSenderId = message.senderId
        )
    }

}
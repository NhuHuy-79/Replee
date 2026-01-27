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

    fun observeConversationById(conversationId: String): Flow<ConversationAndUser?> {
        return conversationDao.observeConversationById(conversationId)
    }

    suspend fun getConversationById(conversationId: String): ConversationAndUser? {
        return conversationDao.getConversationById(conversationId)
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

    suspend fun updateMutedStatus(conversationId: String, muted: Boolean) {
        conversationDao.updateMutedStatus(conversationId, muted)
    }

    suspend fun updateDeleteStatus(conversationId: String, deleted: Boolean) {
        conversationDao.updateDeleteStatus(conversationId, deleted)
    }

    suspend fun updateBlockStatus(conversationId: String, blocked: Boolean) {
        conversationDao.updateBlockStatus(conversationId, blocked)
    }

    suspend fun updateConversationSyncedStatus(conversationId: String, synced: Boolean) {
        conversationDao.updateSyncedStatus(conversationId, synced)
    }

    suspend fun updateSyncStatusOfConversations(conversationIds: List<String>, synced: Boolean) {
        conversationDao.updateSyncedStatusOfConversations(conversationIds, synced)
    }

    suspend fun updatePinnedStatus(conversationId: String, pinned: Boolean) {
        conversationDao.updatePinnedStatus(conversationId, pinned)
    }

    suspend fun updateLastMessage(message: MessageEntity){
        conversationDao.updateLastMessage(
            conversationId = message.conversationId,
            lastMessageTime = message.sentAt ?: -1L,
            lastMessageContent = message.content,
            lastSenderId = message.senderId
        )
    }

    suspend fun getUnSyncedConversations(): List<ConversationAndUser> {
        return conversationDao.getUnSyncedConversation()
    }

    suspend fun updateOwnerNickName(conversationId: String, nickname: String) {
        return conversationDao.updateOwnerNickname(conversationId = conversationId, nickname)
    }

    suspend fun updateOtherUserNickname(conversationId: String, nickname: String) {
        return conversationDao.updateOtherUserNickname(conversationId = conversationId, nickname)
    }

}
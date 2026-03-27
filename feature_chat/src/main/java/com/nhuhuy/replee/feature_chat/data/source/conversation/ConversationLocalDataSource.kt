package com.nhuhuy.replee.feature_chat.data.source.conversation

import com.nhuhuy.replee.core.database.entity.conversation.ConversationAndUser
import com.nhuhuy.replee.core.database.entity.conversation.ConversationDao
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ConversationLocalDataSource {
    suspend fun upsertConversations(entities: List<ConversationEntity>)
    suspend fun getOtherUserInConversation(uid: String): List<String>
    suspend fun upsertAndDeleteConversations(
        upsert: List<ConversationEntity>,
        delete: List<String>,
    )
    suspend fun clearUnreadMessages(conversationId: String)
    fun observeOtherUserInConversation(currentUserId: String): Flow<List<String>>

    suspend fun deleteConversationsByUid(uid: String)
    suspend fun upsertConversation(conversation: ConversationEntity)
    suspend fun deleteConversation(conversationId: String)
    fun observeConversationById(conversationId: String): Flow<ConversationAndUser?>
    suspend fun getConversationById(conversationId: String): ConversationAndUser?
    suspend fun getConversationAndUserById(
        ownerId: String,
        otherUserId: String
    ): ConversationAndUser

    fun observeConversationAndUsers(uid: String): Flow<List<ConversationAndUser>>
    suspend fun updateLastSyncedTime(conversationIds: List<String>, lastSyncedTime: Long)
    suspend fun getConversationsCount(ownerId: String): Int
    suspend fun updateMutedStatus(conversationId: String, muted: Boolean)
    suspend fun updateDeleteStatus(conversationId: String, deleted: Boolean)
    suspend fun updateBlockStatus(conversationId: String, blocked: Boolean)
    suspend fun updateConversationSyncedStatus(conversationId: String, synced: Boolean)
    suspend fun updateSyncStatusOfConversations(conversationIds: List<String>, synced: Boolean)
    suspend fun updatePinnedStatus(conversationId: String, pinned: Boolean)
    suspend fun updateLastMessage(message: MessageEntity)
    suspend fun getUnSyncedConversations(): List<ConversationAndUser>
    suspend fun updateOwnerNickName(conversationId: String, nickname: String)
    suspend fun updateOtherUserNickname(conversationId: String, nickname: String)
}

class ConversationLocalDataSourceImp @Inject constructor(
    private val conversationDao: ConversationDao
) : ConversationLocalDataSource {
    override suspend fun upsertConversations(entities: List<ConversationEntity>) {
        conversationDao.upsertAll(entities)
    }

    override suspend fun getOtherUserInConversation(uid: String): List<String> {
        return conversationDao.getOtherUserInConversationList(uid)
    }

    override suspend fun upsertAndDeleteConversations(
        upsert: List<ConversationEntity>,
        delete: List<String>,
    ) {
        conversationDao.upsertAndDeleteConversations(
            upsert = upsert,
            delete = delete,
        )
    }

    override suspend fun clearUnreadMessages(conversationId: String) {
        conversationDao.clearUnreadMessages(conversationId)
    }

    override fun observeOtherUserInConversation(currentUserId: String): Flow<List<String>> {
        return conversationDao.getOtherUserInConversationFlow(currentUserId)
    }

    override suspend fun deleteConversationsByUid(uid: String) {
        conversationDao.deleteConversationsByOwnerId(uid)
    }

    override suspend fun upsertConversation(conversation: ConversationEntity) {
        conversationDao.upsert(conversation)
    }

    override suspend fun deleteConversation(conversationId: String) {
        conversationDao.deleteConversationById(conversationId)
    }

    override fun observeConversationById(conversationId: String): Flow<ConversationAndUser?> {
        return conversationDao.observeConversationById(conversationId)
    }

    override suspend fun getConversationById(conversationId: String): ConversationAndUser? {
        return conversationDao.getConversationById(conversationId)
    }

    override suspend fun getConversationAndUserById(
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


    override fun observeConversationAndUsers(uid: String): Flow<List<ConversationAndUser>> {
        return conversationDao.observeConversations(uid).map { conversationAndUsers ->
            conversationAndUsers
        }
    }

    override suspend fun updateLastSyncedTime(
        conversationIds: List<String>,
        lastSyncedTime: Long
    ) {
        conversationDao.updateSyncedTime(conversationIds, lastSyncedTime)
    }

    private fun createConversationId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString(separator = "_")
    }

    override suspend fun getConversationsCount(ownerId: String): Int {
        return conversationDao.getConversationListCount(ownerId)
    }

    override suspend fun updateMutedStatus(conversationId: String, muted: Boolean) {
        conversationDao.updateMutedStatus(conversationId, muted)
    }

    override suspend fun updateDeleteStatus(conversationId: String, deleted: Boolean) {
        conversationDao.updateDeleteStatus(conversationId, deleted)
    }

    override suspend fun updateBlockStatus(conversationId: String, blocked: Boolean) {
        conversationDao.updateBlockStatus(conversationId, blocked)
    }

    override suspend fun updateConversationSyncedStatus(conversationId: String, synced: Boolean) {
        conversationDao.updateSyncedStatus(conversationId, synced)
    }

    override suspend fun updateSyncStatusOfConversations(
        conversationIds: List<String>,
        synced: Boolean
    ) {
        conversationDao.updateSyncedStatusOfConversations(conversationIds, synced)
    }

    override suspend fun updatePinnedStatus(conversationId: String, pinned: Boolean) {
        conversationDao.updatePinnedStatus(conversationId, pinned)
    }

    override suspend fun updateLastMessage(message: MessageEntity) {
        conversationDao.updateLastMessage(
            conversationId = message.conversationId,
            lastMessageId = message.messageId,
            lastMessageTime = message.sentAt ?: -1L,
            lastMessageContent = message.content,
            lastSenderId = message.senderId,
            lastMessageType = message.type
        )
    }

    override suspend fun getUnSyncedConversations(): List<ConversationAndUser> {
        return conversationDao.getUnSyncedConversation()
    }

    override suspend fun updateOwnerNickName(conversationId: String, nickname: String) {
        return conversationDao.updateOwnerNickname(conversationId = conversationId, nickname)
    }

    override suspend fun updateOtherUserNickname(conversationId: String, nickname: String) {
        return conversationDao.updateOtherUserNickname(conversationId = conversationId, nickname)
    }

}

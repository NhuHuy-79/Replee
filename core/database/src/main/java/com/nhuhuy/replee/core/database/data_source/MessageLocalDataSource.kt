package com.nhuhuy.replee.core.database.data_source

import androidx.room.withTransaction
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.file_path.MessageWithLocalPath
import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.core.database.mapper.toMessageEntity
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.model.chat.MessageType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface MessageLocalDataSource {
    // --- CREATE / UPSERT ---
    suspend fun upsertMessage(message: MessageEntity)
    suspend fun upsertMessages(messages: List<MessageEntity>)
    suspend fun upsertAndDeleteMessages(upsert: List<Message>, delete: List<String>)
    suspend fun getLastSyncedMessage(conversationId: String): MessageEntity?
    suspend fun getMessageById(messageId: String): MessageEntity?
    suspend fun getMessageListById(messageIds: List<String>): List<MessageEntity>
    suspend fun getUnsyncedMessageByType(messageType: MessageType): List<MessageEntity>
    suspend fun getUnsyncedMessages(): List<MessageEntity>
    suspend fun getIndexOfMessage(conversationId: String, messageId: String): Int
    suspend fun getMessagesByQuery(conversationId: String, query: String): List<MessageEntity>
    suspend fun getNewestMessageInConversation(conversationId: String): MessageEntity?
    fun observeMessages(conversationId: String): Flow<List<MessageWithLocalPath>>
    fun observeMessagesWithQuery(conversationId: String, query: String): Flow<List<MessageEntity>>
    fun observePinnedMessages(conversationId: String): Flow<List<MessageEntity>>

    // --- UPDATE ---
    suspend fun updatePinStatus(messageId: String, pinned: Boolean)
    suspend fun updateMessageStatus(status: MessageStatus, messageId: String)
    suspend fun updateMessageListStatus(status: MessageStatus, messageIds: List<String>)
    suspend fun updateSyncStatus(messageIds: List<String>, status: MessageStatus)
    suspend fun updateRemoteUrlMessage(messageIdWithUrl: Map<String, String>)
    suspend fun updateRemoteUrlMessage(messageId: String, remoteUrl: String, status: MessageStatus)
    suspend fun updateReactions(
        messageId: String,
        ownerReactions: List<String>,
        otherUserReactions: List<String>
    )

    // --- DELETE ---
    suspend fun deleteMessage(message: MessageEntity)
    suspend fun deleteMessageByConversationId(limit: Int)
    fun observeMessagesAroundMessageId(
        conversationId: String,
        anchorMessageId: String,
        limit: Int
    ): Flow<List<MessageWithLocalPath>>
}

class MessageLocalDataSourceImp @Inject constructor(
    private val coreDatabase: CoreDatabase
) : MessageLocalDataSource {
    private val messageDao: MessageDao = coreDatabase.provideMessageDao()
    private val conversationDao = coreDatabase.provideConversationDao()

    // --- CREATE / UPSERT ---

    override suspend fun upsertMessage(message: MessageEntity) {
        messageDao.upsert(message)
    }

    override suspend fun upsertMessages(messages: List<MessageEntity>) {
        messageDao.upsertAll(messages)
    }

    override suspend fun upsertAndDeleteMessages(
        upsert: List<Message>,
        delete: List<String>
    ) {
        messageDao.upsertAndDeleteMessages(
            networkMessages = upsert.map { it.toMessageEntity() },
            deleteIds = delete
        )
    }

    // --- READ ---
    override suspend fun getLastSyncedMessage(conversationId: String): MessageEntity? {
        return messageDao.getLatestMessage(conversationId)
    }

    override suspend fun getIndexOfMessage(conversationId: String, messageId: String): Int {
        return messageDao.getIndexOfMessage(conversationId = conversationId, messageId = messageId)
    }
    override fun observePinnedMessages(conversationId: String): Flow<List<MessageEntity>> {
        return messageDao.observePinnedMessages(conversationId)
    }

    override suspend fun getMessageById(messageId: String): MessageEntity? {
        return messageDao.getMessageById(messageId)
    }

    override suspend fun getMessageListById(messageIds: List<String>): List<MessageEntity> {
        return messageDao.getMessageListById(messageIds)
    }

    override suspend fun getUnsyncedMessageByType(
        messageType: MessageType
    ): List<MessageEntity> {
        return messageDao.getUnSyncedMessageByType(messageType.name)
    }

    override suspend fun getUnsyncedMessages(): List<MessageEntity> {
        return messageDao.getFailedMessages()
    }

    override suspend fun getMessagesByQuery(
        conversationId: String,
        query: String
    ): List<MessageEntity> {
        return messageDao.getMessageByQuery(
            conversationId = conversationId,
            query = query
        )
    }

    override suspend fun getNewestMessageInConversation(conversationId: String): MessageEntity? {
        return messageDao.getNewestMessageInConversation(conversationId)
    }

    override fun observeMessages(conversationId: String) =
        messageDao.observeMessageByConversationId(conversationId)

    override fun observeMessagesWithQuery(
        conversationId: String,
        query: String
    ): Flow<List<MessageEntity>> {
        return messageDao.observeMessagesWithQuery(conversationId, query)
    }

    // --- UPDATE ---

    override suspend fun updateMessageStatus(status: MessageStatus, messageId: String) {
        messageDao.updateStatusOfMessage(
            status = status.name,
            messageId = messageId,
        )
    }

    override suspend fun updateMessageListStatus(status: MessageStatus, messageIds: List<String>) {
        messageDao.updateStatusOfMessageList(
            status = status.name,
            messageIds = messageIds,
        )
    }

    override suspend fun updateSyncStatus(messageIds: List<String>, status: MessageStatus) {
        messageDao.updateStatusOfMessages(messageIds = messageIds, status = status.name)
    }

    override suspend fun updateRemoteUrlMessage(messageIdWithUrl: Map<String, String>) {
        coreDatabase.withTransaction {
            messageIdWithUrl.forEach { (messageId, remoteUrl) ->
                messageDao.updateRemoteUrlAndStatus(
                    messageId = messageId,
                    remoteUrl = remoteUrl,
                    status = MessageStatus.SYNCED.name
                )
            }
        }
    }

    override suspend fun updatePinStatus(messageId: String, pinned: Boolean) {
        messageDao.updatePinStatus(messageId, pinned)
    }

    override suspend fun updateRemoteUrlMessage(
        messageId: String,
        remoteUrl: String,
        status: MessageStatus
    ) {
        messageDao.updateRemoteUrlAndStatus(
            messageId = messageId,
            remoteUrl = remoteUrl,
            status = status.name
        )
    }

    override suspend fun updateReactions(
        messageId: String,
        ownerReactions: List<String>,
        otherUserReactions: List<String>
    ) {
        messageDao.updateReactions(messageId, ownerReactions, otherUserReactions)
    }

    // --- DELETE ---

    override suspend fun deleteMessage(message: MessageEntity) {
        coreDatabase.withTransaction {
            messageDao.softDeleteMessageById(message.messageId)
            conversationDao.updateLastDeletedMessageId(
                conversationId = message.conversationId,
                messageId = message.messageId
            )
        }
    }

    override suspend fun deleteMessageByConversationId(limit: Int) =
        messageDao.deleteMessageByConversationId(limit)

    override fun observeMessagesAroundMessageId(
        conversationId: String,
        anchorMessageId: String,
        limit: Int
    ): Flow<List<MessageWithLocalPath>> {
        return messageDao.observeMessagesAroundId(
            id = conversationId,
            anchorMessageId = anchorMessageId,
            limit = limit
        )
    }

}

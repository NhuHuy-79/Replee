package com.nhuhuy.replee.feature_chat.data.source.message

import androidx.room.withTransaction
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface MessageLocalDataSource {
    suspend fun getMessageById(messageId: String): MessageEntity?
    suspend fun upsertMessage(message: MessageEntity)
    suspend fun updateMessageStatus(status: MessageStatus, messageId: String)
    suspend fun updateRemoteUrlMessage(messageIdWithUrl: Map<String, String>)
    suspend fun updateRemoteUrlMessage(
        messageId: String,
        remoteUrl: String,
        status: MessageStatus
    )
    suspend fun getUnsyncedMessageByType(messageType: MessageType): List<MessageEntity>
    suspend fun updateMessageListStatus(status: MessageStatus, messageIds: List<String>)
    suspend fun upsertMessages(messages: List<MessageEntity>)
    suspend fun upsertAndDeleteMessages(upsert: List<MessageEntity>, delete: List<String>)
    suspend fun getMessagesByQuery(conversationId: String, query: String): List<MessageEntity>
    suspend fun updateSyncStatus(messageIds: List<String>, status: MessageStatus)
    suspend fun getUnsyncedMessages(): List<MessageEntity>
    fun observeMessages(conversationId: String): Flow<List<MessageEntity>>
    suspend fun deleteMessageByConversationId(limit: Int)
}

class MessageLocalDataSourceImp @Inject constructor(
    private val coreDatabase: CoreDatabase
) : MessageLocalDataSource {
    private val messageDao: MessageDao = coreDatabase.provideMessageDao()
    override suspend fun getMessageById(messageId: String): MessageEntity? {
        return messageDao.getMessageById(messageId)
    }

    override suspend fun upsertMessage(message: MessageEntity) {
        messageDao.upsert(message)
    }

    override suspend fun updateMessageStatus(status: MessageStatus, messageId: String) {
        messageDao.updateStatusOfMessage(
            status = status.name,
            messageId = messageId,
        )
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

    override suspend fun updateRemoteUrlMessage(
        messageId: String,
        remoteUrl: String,
        status: MessageStatus
    ) {
        return messageDao.updateRemoteUrlAndStatus(
            messageId = messageId,
            remoteUrl = remoteUrl,
            status = status.name
        )
    }

    override suspend fun getUnsyncedMessageByType(
        messageType: MessageType
    ): List<MessageEntity> {
        return messageDao.getUnSyncedMessageByType(messageType.name)
    }

    override suspend fun updateMessageListStatus(status: MessageStatus, messageIds: List<String>) {
        messageDao.updateStatusOfMessageList(
            status = status.name,
            messageIds = messageIds,
        )
    }

    override suspend fun upsertMessages(messages: List<MessageEntity>) {
        messageDao.upsertAll(messages)
    }

    override suspend fun upsertAndDeleteMessages(
        upsert: List<MessageEntity>,
        delete: List<String>
    ) {
        messageDao.upsertAndDeleteMessages(
            upsert = upsert,
            delete = delete
        )
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

    override suspend fun updateSyncStatus(messageIds: List<String>, status: MessageStatus) {
        messageDao.updateStatusOfMessages(messageIds = messageIds, status = status.name)
    }

    override suspend fun getUnsyncedMessages(): List<MessageEntity> {
        return messageDao.getFailedMessages()
    }

    override fun observeMessages(conversationId: String) =
        messageDao.observeMessageByConversationId(conversationId)

    override suspend fun deleteMessageByConversationId(limit: Int) =
        messageDao.deleteMessageByConversationId(limit)

}

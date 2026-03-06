package com.nhuhuy.replee.feature_chat.data.source.message

import androidx.room.withTransaction
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class MessageLocalDataSource @Inject constructor(
    private val coreDatabase: CoreDatabase
) {
    private val messageDao: MessageDao = coreDatabase.provideMessageDao()
    suspend fun upsertMessage(message: MessageEntity) {
        messageDao.upsert(message)
    }

    suspend fun updateMessageStatus(status: MessageStatus, messageId: String) {
        messageDao.updateStatusOfMessage(
            status = status.name,
            messageId = messageId,
        )
    }

    suspend fun updateRemoteUrlMessage(messageIdWithUrl: Map<String, String>) {
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

    suspend fun getUnsyncedMessageByType(
        messageType: MessageType
    ): List<MessageEntity> {
        return messageDao.getUnSyncedMessageByType(messageType.name)
    }

    suspend fun updateMessageListStatus(status: MessageStatus, messageIds: List<String>) {
        messageDao.updateStatusOfMessageList(
            status = status.name,
            messageIds = messageIds,
        )
    }

    suspend fun upsertMessages(messages: List<MessageEntity>){
        messageDao.upsertAll(messages)
    }

    suspend fun upsertAndDeleteMessages(
        upsert: List<MessageEntity>,
        delete: List<String>
    ) {
        messageDao.upsertAndDeleteMessages(
            upsert = upsert,
            delete = delete
        )
    }

    suspend fun getMessagesByQuery(conversationId: String, query: String) : List<MessageEntity>{
        return messageDao.getMessageByQuery(
            conversationId = conversationId,
            query = query
        )
    }

    suspend fun updateSyncStatus(messageIds: List<String>, status: MessageStatus){
        messageDao.updateStatusOfMessages(messageIds = messageIds, status = status.name)
    }

    suspend fun getUnsyncedMessages() : List<MessageEntity>{
        return messageDao.getFailedMessages()
    }

    fun observeMessages(conversationId: String) = messageDao.observeMessageByConversationId(conversationId)

    suspend fun deleteMessageByConversationId(limit: Int) = messageDao.deleteMessageByConversationId(limit)

}
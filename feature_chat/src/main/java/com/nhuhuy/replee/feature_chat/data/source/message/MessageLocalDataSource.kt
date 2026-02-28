package com.nhuhuy.replee.feature_chat.data.source.message

import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import javax.inject.Inject

class MessageLocalDataSource @Inject constructor(
    private val messageDao: MessageDao
) {
    suspend fun upsertMessage(message: MessageEntity) {
        messageDao.upsert(message)
    }

    suspend fun updateMessageStatus(status: MessageStatus, messageId: String) {
        messageDao.updateStatusOfMessage(
            status = status.name,
            messageId = messageId,
        )
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

    fun observePagingSource(conversationId: String) = messageDao.pagingSource(conversationId)

    suspend fun updateMessageSeenStatus(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String
    ){
        messageDao.markMessageAsRead(
            messageIds = messageIds,
            conversationId = conversationId,
            receiverId = receiverId
        )
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
package com.nhuhuy.replee.feature_chat.data.source.chat

import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import javax.inject.Inject

class MessageLocalDataSource @Inject constructor(
    private val messageDao: MessageDao
) {
    suspend fun saveMessage(message: MessageEntity) {
        messageDao.upsert(message)
    }

    suspend fun saveAllMessages(messages: List<MessageEntity>){
        messageDao.upsertAll(messages)
    }

    suspend fun markMessageAsRead(
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

    suspend fun updateSyncStatus(messageIds: List<String>, status: MessageStatus){
        messageDao.updateStatusOfMessages(messageIds = messageIds, status = status.name)
    }

    suspend fun getFailedMessages() : List<MessageEntity>{
        return messageDao.getFailedMessages()
    }

    fun observeMessages(conversationId: String) = messageDao.observeMessageByConversationId(conversationId)

    suspend fun deleteMessageWithConversationId(limit: Int) = messageDao.deleteMessageByConversationId(limit)

}
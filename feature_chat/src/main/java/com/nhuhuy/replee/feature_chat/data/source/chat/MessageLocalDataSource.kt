package com.nhuhuy.replee.feature_chat.data.source.chat

import com.nhuhuy.replee.core.database.entity.message.MessageDao
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
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

    fun observeMessages(conversationId: String) = messageDao.observeMessageByConversationId(conversationId)

}
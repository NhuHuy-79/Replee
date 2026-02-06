package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val logger: Logger,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val dispatcher: CoroutineDispatcher,
) : MessageRepository,
    NetworkResultCaller(dispatcher, logger) {
    override suspend fun sendMessage(
        message: Message
    ): NetworkResult<String> {
        return safeCallWithTimeout {
            messageLocalDataSource.upsertMessage(message = message.toMessageEntity())

            messageNetworkDataSource.sendMessage(message = message.toMessageDTO())

            message.messageId
        }
    }

    override fun observeNetworkMessageList(conversationId: String): Flow<NetworkResult<List<Message>>> {
        return messageNetworkDataSource.streamMessageListByConversationId(conversationId)
            .map { messageList ->
                val data = messageList.map { messageDTO -> messageDTO.toMessage() }
                NetworkResult.Success(data) as NetworkResult<List<Message>>
            }
            .catch { throwable ->
                emit(NetworkResult.Failure(throwable))
            }
    }

    override fun observeLocalMessages(conversationId: String): Flow<List<Message>> {
        return messageLocalDataSource.observeMessages(conversationId).map { entities ->
            entities.map { entity ->
                entity.toMessage()
            }
        }.flowOn(dispatcher)
    }

    override suspend fun markMessagesAsRead(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String
    ): NetworkResult<Unit> = safeCallWithTimeout {
        messageLocalDataSource.updateMessageSeenStatus(
            messageIds = messageIds,
            conversationId = conversationId,
            receiverId = receiverId
        )
        val conversationDTO = conversationNetworkDataSource.fetchConversationById(conversationId)
        val receiverField =
            if (conversationDTO?.user1?.uid == receiverId) "user1" else "user2"
        val count = messageNetworkDataSource.updateMessageSeenStatus(
            conversationId = conversationId,
            messageIds = messageIds,
            receiverId = receiverId
        )
        conversationNetworkDataSource.updateUnreadMessageCount(
            conversationId = conversationId,
            receiverField = receiverField,
            count = count
        )
    }

    override suspend fun saveMessages(messages: List<Message>) {
        withContext(dispatcher){
            val entities = messages.map { message -> message.toMessageEntity() }
            messageLocalDataSource.upsertMessages(entities)
        }
    }

    override suspend fun searchMessageWithQuery(
        conversationId: String,
        query: String
    ): List<Message> {
        return withContext(dispatcher){
            messageLocalDataSource.getMessagesByQuery(
                conversationId = conversationId,
                query = query
            ).map { entity -> entity.toMessage() }
        }
    }
}
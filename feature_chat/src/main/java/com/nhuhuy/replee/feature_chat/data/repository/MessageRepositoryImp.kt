package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.mapResource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val dispatcher: CoroutineDispatcher,
) : MessageRepository {
    override fun observeNetworkMessages(conversationId: String): Flow<Resource<List<Message>, RemoteFailure>> {
        return messageNetworkDataSource.streamMessagesByConversationId(conversationId)
            .mapResource { messageList ->
                messageList.map { messageDTO -> messageDTO.toMessage() }
            }.flowOn(dispatcher)
    }

    override suspend fun fetchMessages(conversationId: String): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                val messages = messageNetworkDataSource.fetchMessagesByConversationId(conversationId)
                    .map { dTO ->
                        dTO.toMessage().toMessageEntity()
                    }
                messageLocalDataSource.upsertMessages(messages)
            }
        }
    }

    override fun observeLocalMessages(conversationId: String): Flow<List<Message>> {
        return messageLocalDataSource.observeMessages(conversationId).map { entities ->
            entities.map { entity ->
                entity.toMessage()
            }
        }.flowOn(dispatcher)
    }

    override suspend fun sendMessage(
        message: Message,
        conversationId: String
    ): Resource<Message, RemoteFailure> {
        return withContext(dispatcher) {
            val entity = message.toMessageEntity()
            messageLocalDataSource.upsertMessage(entity)
            conversationLocalDataSource.updateLastMessage(entity)
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                },
            ) {
                val messageDTO = message.toMessageDTO()
                messageNetworkDataSource.sendMessage(messageDTO, conversationId)
                val conversationDTO = conversationNetworkDataSource.fetchConversationById(conversationId)
                conversationNetworkDataSource.updateLastMessage(messageDTO, conversationDTO)
                //LOCAL
                message
            }
        }
    }

    override suspend fun markMessageAsRead(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String
    ): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                },
            ) {
                messageLocalDataSource.updateMessageSeenStatus(
                    messageIds = messageIds,
                    conversationId = conversationId,
                    receiverId = receiverId
                )


                val conversationDTO = conversationNetworkDataSource.fetchConversationById(conversationId)
                val receiverField =
                    if (conversationDTO.user1.uid == receiverId) "user1" else "user2"
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
        }
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
package com.nhuhuy.replee.feature_chat.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.firebase.model.DataChange
import com.nhuhuy.replee.core.firebase.model.mapData
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageRemoteMediator
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val coreDatabase: CoreDatabase,
    private val logger: Logger,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
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

    override fun observeNetworkMessageChange(conversationId: String): Flow<List<DataChange<Message>>> {
        return messageNetworkDataSource.listenMessageChangesByConversationId(conversationId)
            .map { dataChanges ->
                Timber.d("Message Change : $dataChanges")
                dataChanges.map { dataChange ->
                    dataChange.mapData { messageDTO ->
                        messageDTO.toMessage()
                    }
                }
            }
    }

    override suspend fun updateLocalDataChange(
        upsert: List<Message>,
        delete: List<String>
    ) {
        withContext(dispatcher) {
            val mappedUpsert = upsert.map { message -> message.toMessageEntity() }
            if (upsert.isEmpty() && delete.isEmpty()) {
                Timber.e("$upsert - $delete")
                return@withContext
            } else {
                messageLocalDataSource.upsertAndDeleteMessages(
                    upsert = mappedUpsert,
                    delete = delete
                )
            }
        }
    }

    override suspend fun fetchMessageWithPaging(
        conversationId: String,
        limit: Int,
        startAfterKey: Long?
    ): NetworkResult<List<Message>> {
        TODO("")
    }

    override fun observeMessageChangeWithPaging(
        conversationId: String,
        limit: Int
    ): Flow<List<DataChange<Message>>> {
        return messageNetworkDataSource.listenToMessagesWithLimit(conversationId, limit)
            .map { dataChanges ->
                dataChanges.map { dataChange ->
                    dataChange.mapData { messageDTO ->
                        messageDTO.toMessage()
                    }
                }
            }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun observeMessageWithPaging(
        conversationId: String,
    ): Flow<PagingData<Message>> {
        val messageDao = coreDatabase.provideMessageDao()
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 1
            ),
            remoteMediator = MessageRemoteMediator(
                conversationId = conversationId,
                db = coreDatabase,
                network = messageNetworkDataSource
            )
        ) {
            messageDao.pagingSource(conversationId)
        }.flow.map { pagingData ->
            pagingData.map { messageEntity ->
                messageEntity.toMessage()
            }
        }
    }
}
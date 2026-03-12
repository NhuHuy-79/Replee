package com.nhuhuy.replee.feature_chat.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.mapData
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageRemoteMediator
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
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
    private val uploadFileService: UploadFileService,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : MessageRepository {
    override suspend fun sendMessage(
        message: Message
    ): NetworkResult<String> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            val entity = message.toMessageEntity()
            messageLocalDataSource.upsertMessage(message = entity)
            conversationLocalDataSource.updateLastMessage(message = entity)

            messageNetworkDataSource.sendMessage(message = message.toMessageDTO())

            val conversationDTO =
                conversationNetworkDataSource.fetchConversationById(message.conversationId)
            val messageDTO = message.toMessageDTO()

            if (conversationDTO == null) {
                conversationLocalDataSource.updateSyncStatusOfConversations(
                    conversationIds = listOf(message.conversationId),
                    synced = false
                )
            } else {
                conversationNetworkDataSource.updateLastMessage(messageDTO, conversationDTO)
            }

            message.messageId
        }
    }

    override suspend fun sendImage(
        rawMessage: Message,
        uriPath: String,
    ): NetworkResult<String> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            messageLocalDataSource.upsertMessage(rawMessage.toMessageEntity())
            val url = uploadFileService.uploadImageWithUriPath(uriPath)

            if (url.isBlank()) {
                val failure = rawMessage.copy(
                    content = url,
                    type = MessageType.IMAGE,
                    status = MessageStatus.FAILED
                )
                messageLocalDataSource.upsertMessage(failure.toMessageEntity())
            } else {
                val message = rawMessage.copy(
                    content = url,
                    type = MessageType.IMAGE,
                )
                messageLocalDataSource.upsertMessage(message.toMessageEntity())
                messageNetworkDataSource.sendMessage(message.toMessageDTO())

                val conversationDTO =
                    conversationNetworkDataSource.fetchConversationById(message.conversationId)
                val messageDTO = message.toMessageDTO()

                if (conversationDTO == null) {
                    conversationLocalDataSource.updateSyncStatusOfConversations(
                        conversationIds = listOf(message.conversationId),
                        synced = false
                    )
                } else {
                    conversationNetworkDataSource.updateLastMessage(messageDTO, conversationDTO)
                }
            }

            url
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
        }.flowOn(ioDispatcher)
    }

    override suspend fun markMessagesAsRead(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String
    ): NetworkResult<Unit> = executeWithTimeout {
        messageLocalDataSource.updateMessageListStatus(
            status = MessageStatus.SEEN,
            messageIds = messageIds
        )
        val conversationDTO = conversationNetworkDataSource.fetchConversationById(conversationId)
        val receiverField =
            if (conversationDTO?.user1?.uid == receiverId) "user1" else "user2"
        val count = messageNetworkDataSource.updateMessageStatus(
            conversationId = conversationId,
            messageIds = messageIds,
            status = MessageStatus.SEEN
        )
        conversationNetworkDataSource.updateUnreadMessageCount(
            conversationId = conversationId,
            receiverField = receiverField,
            count = count
        )
    }

    override suspend fun searchMessageWithQuery(
        conversationId: String,
        query: String
    ): List<Message> {
        return withContext(ioDispatcher) {
            messageLocalDataSource.getMessagesByQuery(
                conversationId = conversationId,
                query = query
            ).map { entity -> entity.toMessage() }
        }
    }

    override fun observeNetworkMessageChange(conversationId: String): Flow<List<DataChange<Message>>> {
        return messageNetworkDataSource.listenMessageChangesByConversationId(conversationId)
            .map { dataChanges ->
                dataChanges.map { dataChange ->
                    dataChange.mapData { messageDTO ->
                        messageDTO.toMessage()
                    }
                }
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun updateLocalDataChange(
        upsert: List<Message>,
        delete: List<String>
    ) {
        withContext(ioDispatcher) {
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
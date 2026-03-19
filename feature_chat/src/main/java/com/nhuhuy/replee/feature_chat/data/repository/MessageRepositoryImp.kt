package com.nhuhuy.replee.feature_chat.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.common.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.mapData
import com.nhuhuy.replee.core.network.quailify.Cloudinary
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.LocalPathMessageRemoteMediator
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageRemoteMediator
import com.nhuhuy.replee.feature_chat.domain.model.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import com.nhuhuy.replee.feature_chat.domain.model.toLocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val coreDatabase: CoreDatabase,
    @Cloudinary private val uploadFileService: UploadFileService,
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

            val dto = message.toMessageDTO().copy(status = MessageStatus.SYNCED)
            messageNetworkDataSource.sendMessage(message = dto)

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

    override suspend fun saveMessage(message: Message): String {
        messageLocalDataSource.upsertMessage(message.toMessageEntity())
        return message.messageId
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

        val count = messageNetworkDataSource.updateMessageStatus(
            receiverId = receiverId,
            conversationId = conversationId,
            messageIds = messageIds,
            status = MessageStatus.SEEN,

        )
        conversationNetworkDataSource.updateUnreadMessageCount(
            conversationId = conversationId,
            receiverId = receiverId,
            count = count
        )
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

    override suspend fun updateRemoteUrlMessage(
        messageId: String,
        remoteUrl: String,
        status: MessageStatus
    ): Message? {
        return withContext(ioDispatcher) {
            messageLocalDataSource.updateRemoteUrlMessage(
                messageId = messageId,
                remoteUrl = remoteUrl,
                status = status
            )

            messageLocalDataSource.getMessageById(messageId)?.toMessage()
        }
    }

    override fun observeMessageChangeWithLimit(
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
    override fun pagingLocalMessages(
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

    @OptIn(ExperimentalPagingApi::class)
    override fun observeLocalMessageWithPaging(conversationId: String): Flow<PagingData<LocalPathMessage>> {
        val messageDao = coreDatabase.provideMessageDao()
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 1
            ),
            remoteMediator = LocalPathMessageRemoteMediator(
                conversationId = conversationId,
                coreDatabase = coreDatabase,
                messageNetworkDataSource = messageNetworkDataSource
            )
        ) {
            messageDao.getMessagesPagingSource(conversationId)
        }.flow.map { pagingData ->
            pagingData.map { messageEntity ->
                messageEntity.toLocalPathMessage()
            }
        }

    }

    override suspend fun markAllMessagesRead(
        conversationId: String,
        receiverId: String
    ): NetworkResult<Unit> {
        return execute {
            messageLocalDataSource.updateMessageStatusInConversation(
                conversationId = conversationId,
                receiverId = receiverId,
                status = MessageStatus.SEEN
            )

            messageNetworkDataSource.updateReceiverMessageStatus(
                conversationId = conversationId,
                receiverId = receiverId,
                status = MessageStatus.SEEN
            )
        }

    }

}
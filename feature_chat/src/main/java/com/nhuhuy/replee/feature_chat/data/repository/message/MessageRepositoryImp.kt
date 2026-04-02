package com.nhuhuy.replee.feature_chat.data.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.mapData
import com.nhuhuy.replee.feature_chat.data.NotificationHelper
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.LocalPathMessageRemoteMediator
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.message.toLocalPathMessage
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
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val notificationHelper: NotificationHelper
) : MessageRepository {
    override suspend fun getNewestMessageInConversation(conversationId: String): Message? {
        return withContext(ioDispatcher) {
            messageLocalDataSource.getNewestMessageInConversation(conversationId)?.toMessage()
        }
    }

    override suspend fun fetchMessagesByTimestamp(
        conversationId: String,
        timestamp: Long
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            val entities = messageNetworkDataSource.fetchMessagesInConversationByTimestamp(
                conversationId = conversationId,
                timestamp = timestamp
            ).map { messageDTO ->
                messageDTO.toMessage().toMessageEntity()
            }

            messageLocalDataSource.upsertMessages(entities)
        }
    }

    override suspend fun deleteMultipleMessage(messages: List<Message>): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            messageNetworkDataSource.deleteMultipleMessage(
                messages = messages.map { message -> message.toMessageDTO() }
            )
        }
    }

    override suspend fun getMessageListById(messageIds: List<String>): List<Message> {
        return withContext(ioDispatcher) {
            messageLocalDataSource.getMessageListById(messageIds)
                .map { entity -> entity.toMessage() }
        }
    }


    override suspend fun deleteMessage(message: Message): NetworkResult<String> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            messageLocalDataSource.deleteMessageById(message = message.toMessageEntity())
            messageNetworkDataSource.deleteMessage(
                conversationId = message.conversationId,
                messageId = message.messageId
            )

            message.messageId
        }
    }

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
    ): NetworkResult<List<Message>> {
        return execute {
            val entities = messageLocalDataSource.updateMessageStatusInConversation(
                conversationId = conversationId,
                receiverId = receiverId,
                status = MessageStatus.SEEN
            )

            val notificationIds = entities.map { entity ->
                entity.messageId.hashCode()
            }

            notificationHelper.cancelNotificationList(notificationIds = notificationIds)

            messageNetworkDataSource.updateReceiverMessageStatus(
                conversationId = conversationId,
                receiverId = receiverId,
                status = MessageStatus.SEEN
            )

            entities.map { messageEntity -> messageEntity.toMessage() }
        }

    }

}
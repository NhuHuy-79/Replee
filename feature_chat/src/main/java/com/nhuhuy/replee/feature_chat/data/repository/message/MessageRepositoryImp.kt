package com.nhuhuy.replee.feature_chat.data.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val coreDatabase: CoreDatabase,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : MessageRepository {

    // --- CREATE / SEND---

    override suspend fun sendMessage(message: Message): NetworkResult<String> {
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

    // --- READ / OBSERVE ---

    override suspend fun getMessageListById(messageIds: List<String>): List<Message> {
        return withContext(ioDispatcher) {
            messageLocalDataSource.getMessageListById(messageIds)
                .map { entity -> entity.toMessage() }
        }
    }

    override suspend fun getNewestMessageInConversation(conversationId: String): Message? {
        return withContext(ioDispatcher) {
            messageLocalDataSource.getNewestMessageInConversation(conversationId)?.toMessage()
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
                prefetchDistance = 5
            ),
            remoteMediator = LocalPathMessageRemoteMediator(
                conversationId = conversationId,
                coreDatabase = coreDatabase,
                messageNetworkDataSource = messageNetworkDataSource
            )
        ) {
            messageDao.getMessagesPagingSource(conversationId)
        }.flow
            .map { pagingData ->
            pagingData.map { messageEntity ->
                messageEntity.toLocalPathMessage()
            }
        }
            .flowOn(ioDispatcher)
    }

    override fun observePinnedMessages(conversationId: String): Flow<List<Message>> {
        return messageLocalDataSource.observePinnedMessages(conversationId).map { entities ->
            entities.map { entity -> entity.toMessage() }
        }
    }

    override fun observeLocalMessagesWithQuery(
        conversationId: String,
        query: String
    ): Flow<List<Message>> {
        return messageLocalDataSource.observeMessagesWithQuery(
            conversationId = conversationId,
            query = query
        ).map { entities ->
            entities.map { entity ->
                entity.toMessage()
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun updatePinStatusMessage(
        conversationId: String,
        messageId: String,
        pinned: Boolean
    ): NetworkResult<String> {
        return executeWithTimeout(ioDispatcher) {
            messageLocalDataSource.updatePinStatus(
                messageId = messageId,
                pinned = pinned
            )

            messageNetworkDataSource.updatePinStatus(
                conversationId = conversationId,
                messageId = messageId,
                pinned = pinned
            )

            messageId
        }
    }

    // --- UPDATE  ---

    override suspend fun pinMultipleRemoteMessage(
        messages: List<Message>,
        pinned: Boolean
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            messageNetworkDataSource.pinMultipleMessage(
                messages = messages.map { it.toMessageDTO() },
                pinned = pinned
            )
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

    // --- DELETE ---

    override suspend fun deleteMessage(message: Message): NetworkResult<String> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            messageLocalDataSource.deleteMessage(message.toMessageEntity())
            message.messageId
        }
    }

    override suspend fun deleteMultipleMessage(messages: List<Message>): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            messageLocalDataSource.deleteAllMessages(messages)
            messageNetworkDataSource.deleteMultipleMessage(
                messages = messages.map { message -> message.toMessageDTO() }
            )

        }
    }

    // --- SYNC / NETWORK ---

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

    override fun listenMessageChanges(conversationId: String): Flow<Unit> {
        return messageNetworkDataSource.listenMessageChangesByConversationId(conversationId)
            .map { dataChanges ->
                val upserts = mutableListOf<Message>()
                val deletes = mutableListOf<String>()

                for (change in dataChanges) {
                    when (change) {
                        is DataChange.Delete -> deletes.add(change.id)
                        is DataChange.Upsert -> upserts.add(
                            change.data.toMessage()
                        )
                    }
                }

                messageLocalDataSource.upsertAndDeleteMessages(
                    upsert = upserts,
                    delete = deletes
                )

                Timber.d("MESSAGE_SYNC: ${dataChanges.size}")
            }.flowOn(ioDispatcher)
            .catch { exception -> Timber.e(exception) }
    }
}

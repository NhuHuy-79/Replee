package com.nhuhuy.replee.core.data.repository.chat

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.data.paging_source.MessageRemoteMediator
import com.nhuhuy.replee.core.data.paging_source.PinnedMessagePagingSource
import com.nhuhuy.replee.core.data.paging_source.SearchedMessagePagingSource
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.LocalTransactionRunner
import com.nhuhuy.replee.core.database.data_source.ConversationLocalDataSource
import com.nhuhuy.replee.core.database.data_source.MessageLocalDataSource
import com.nhuhuy.replee.core.database.mapper.toLocalPathMessage
import com.nhuhuy.replee.core.database.mapper.toMessage
import com.nhuhuy.replee.core.database.mapper.toMessageEntity
import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.network.data_source.ConversationNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.MessageNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.PagingMessageNetworkDataSource
import com.nhuhuy.replee.core.network.mapper.toMessageDTO
import com.nhuhuy.replee.core.network.model.DataChange
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import com.nhuhuy.replee.core.network.mapper.toMessage as toMessageNetwork

class MessageRepositoryImp @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val localTransactionRunner: LocalTransactionRunner,
    private val coreDatabase: CoreDatabase,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val pagingMessageNetworkDataSource: PagingMessageNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val sessionManager: SessionManager,

) : MessageRepository {

    // --- CREATE / SEND---

    override suspend fun sendMessage(message: Message): NetworkResult<String> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            localTransactionRunner.runInTransaction {
                val entity = message.toMessageEntity()
                messageLocalDataSource.upsertMessage(message = entity)
                conversationLocalDataSource.updateLastMessage(message = entity)
            }
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

    override suspend fun getIndexOfMessage(conversationId: String, messageId: String): Int {
        return messageLocalDataSource.getIndexOfMessage(
            conversationId = conversationId,
            messageId = messageId
        )
    }

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

    override fun observePinnedMessages(
        conversationId: String,
        currentUserId: String
    ): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
        ) {
            PinnedMessagePagingSource(
                conversationId = conversationId,
                currentUserId = currentUserId,
                messageNetworkDataSource = messageNetworkDataSource,
                conversationNetworkDataSource = conversationNetworkDataSource
            )
        }.flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun observeLocalMessageWithPaging(
        anchorMessageId: String?,
        conversationId: String
    ): Flow<PagingData<LocalPathMessage>> {
        val messageDao = coreDatabase.provideMessageDao()
        return Pager(
            config = PagingConfig(
                pageSize = if (anchorMessageId == null) 20 else 15,
                initialLoadSize = 60,
                enablePlaceholders = false,
                prefetchDistance = if (anchorMessageId == null) 5 else 1
            ),
            remoteMediator = MessageRemoteMediator(
                currentUserId = sessionManager.getUserIdOrNull(),
                messageIdToJump = anchorMessageId,
                conversationId = conversationId,
                coreDatabase = coreDatabase,
                pagingMessageNetworkDataSource = pagingMessageNetworkDataSource
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

    override fun observeMessagesWithQuery(
        currentUserId: String,
        conversationId: String,
        query: String
    ): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            )
        ) {
            SearchedMessagePagingSource(
                currentUserId = currentUserId,
                conversationId = conversationId,
                searchQuery = query,
                messageNetworkDataSource = messageNetworkDataSource,
                conversationNetworkDataSource = conversationNetworkDataSource
            )
        }.flow
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

    override suspend fun addReaction(
        conversationId: String,
        messageId: String,
        userId: String,
        reaction: String
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            val entity = messageLocalDataSource.getMessageById(messageId)
            entity?.let {
                val currentUserId = sessionManager.getUserIdOrNull()
                val isOwner = userId == currentUserId

                val newOwnerReactions =
                    if (isOwner) it.ownerReactions + reaction else it.ownerReactions
                val newOtherUserReactions =
                    if (!isOwner) it.otherUserReactions + reaction else it.otherUserReactions

                messageLocalDataSource.updateReactions(
                    messageId,
                    newOwnerReactions,
                    newOtherUserReactions
                )

            }
            messageNetworkDataSource.addReaction(conversationId, messageId, userId, reaction)
        }
    }

    override suspend fun removeReaction(
        conversationId: String,
        messageId: String,
        userId: String,
        reaction: String
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            val entity = messageLocalDataSource.getMessageById(messageId)
            entity?.let {
                val currentUserId = sessionManager.getUserIdOrNull()
                val isOwner = userId == currentUserId

                val newOwnerReactions =
                    if (isOwner) it.ownerReactions - reaction else it.ownerReactions
                val newOtherUserReactions =
                    if (!isOwner) it.otherUserReactions - reaction else it.otherUserReactions

                messageLocalDataSource.updateReactions(
                    messageId,
                    newOwnerReactions,
                    newOtherUserReactions
                )
            }
            messageNetworkDataSource.removeReaction(conversationId, messageId, userId, reaction)
        }
    }

    override suspend fun updateReactionMultiMessage(messages: List<Message>): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            val currentUserId = sessionManager.getUserIdOrNull()
            messageNetworkDataSource.updateReactionMultiMessage(
                messages = messages.map { it.toMessageDTO(currentUserId) }
            )
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
            val currentUserId = sessionManager.getUserIdOrNull()
            val entities = messageNetworkDataSource.fetchMessagesInConversationByTimestamp(
                conversationId = conversationId,
                timestamp = timestamp
            ).map { messageDTO ->
                messageDTO.toMessageNetwork(currentUserId).toMessageEntity()
            }

            messageLocalDataSource.upsertMessages(entities)
        }
    }

    override fun listenMessageChanges(conversationId: String): Flow<Unit> {
        val currentUserId = sessionManager.getUserIdOrNull()
        return messageNetworkDataSource.listenMessageChangesByConversationId(conversationId)
            .map { dataChanges ->
                val upserts = mutableListOf<Message>()
                val deletes = mutableListOf<String>()

                for (change in dataChanges) {
                    when (change) {
                        is DataChange.Delete -> deletes.add(change.id)
                        is DataChange.Upsert -> upserts.add(
                            change.data.toMessageNetwork(currentUserId)
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

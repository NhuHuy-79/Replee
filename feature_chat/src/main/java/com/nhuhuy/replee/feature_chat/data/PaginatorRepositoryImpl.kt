package com.nhuhuy.replee.feature_chat.data

import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.data_source.MessageLocalDataSource
import com.nhuhuy.replee.core.database.mapper.toLocalPathMessage
import com.nhuhuy.replee.core.database.mapper.toMessageEntity
import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.network.data_source.message.PagingMessageNetworkDataSource
import com.nhuhuy.replee.core.network.mapper.toMessage
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class PaginatorRepositoryImpl @Inject constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val sessionManager: SessionManager,
    private val pagingMessageNetworkDataSource: PagingMessageNetworkDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
) : PaginatorRepository {
    private val currentUserId: String? get() = sessionManager.getUserIdOrNull()
    override fun observeLocalMessageAroundKey(
        key: String,
        conversationId: String,
        limit: Int
    ): Flow<List<LocalPathMessage>> {
        return messageLocalDataSource.observeMessagesAroundMessageId(
            conversationId = conversationId,
            anchorMessageId = key,
            limit = limit
        ).map { messages ->
            messages.map { it.toLocalPathMessage() }
        }.flowOn(ioDispatcher)
    }

    override fun observeLocalMessages(conversationId: String): Flow<List<LocalPathMessage>> {
        return messageLocalDataSource.observeMessages(conversationId).map { entities ->
            Timber.e("MessageSize: ${entities.size}")
            entities.map { entity -> entity.toLocalPathMessage() }
        }.flowOn(ioDispatcher)
    }

    override fun observeMessagesAround(
        conversationId: String,
        startTime: Long?,
        endTime: Long?
    ): Flow<List<LocalPathMessage>> {
        return messageLocalDataSource.observeMessagesAround(conversationId, startTime, endTime)
            .map { entities ->
                entities.map { entity -> entity.toLocalPathMessage() }
            }.flowOn(ioDispatcher)
    }

    override suspend fun getCurrentKey(conversationId: String): String? {
        return messageLocalDataSource.getLastSyncedMessage(conversationId)?.messageId
    }

    override suspend fun fetchLatestMessage(
        conversationId: String,
        limit: Long
    ): NetworkResult<List<Message>> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            val messageDTOs = pagingMessageNetworkDataSource.fetchInitialMessageList(
                conversationId = conversationId,
                pageSize = limit
            )
            val messages = messageDTOs.map { messageDTO -> messageDTO.toMessage(currentUserId) }
            val messageEntities = messages.map { message -> message.toMessageEntity() }
            messageLocalDataSource.upsertMessages(messageEntities)
            messages
        }
    }

    override suspend fun fetchMessageBeforeKey(
        conversationId: String,
        key: String,
        limit: Long
    ): NetworkResult<List<Message>> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            val messageDTOs = pagingMessageNetworkDataSource.fetchMessageListBeforeAnchor(
                conversationId = conversationId,
                messageId = key,
                pageSize = limit
            )
            val messages = messageDTOs.map { messageDTO -> messageDTO.toMessage(currentUserId) }
            val messageEntities = messages.map { message -> message.toMessageEntity() }
            messageLocalDataSource.upsertMessages(messageEntities)
            messages
        }
    }

    override suspend fun fetchMessageAfterKey(
        conversationId: String,
        key: String,
        limit: Long
    ): NetworkResult<List<Message>> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            val messageDTOs = pagingMessageNetworkDataSource.fetchMessageListAfterAnchor(
                conversationId = conversationId,
                messageId = key,
                pageSize = limit
            )
            val messages = messageDTOs.map { messageDTO -> messageDTO.toMessage(currentUserId) }
            val messageEntities = messages.map { message -> message.toMessageEntity() }
            messageLocalDataSource.upsertMessages(messageEntities)
            messages
        }
    }

    override suspend fun fetchMessageBetweenKey(
        conversationId: String,
        key: String,
        limit: Long
    ): NetworkResult<List<Message>> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            val messageDTOs = pagingMessageNetworkDataSource.fetchMessageListAroundAnchor(
                conversationId = conversationId,
                messageId = key,
                pageSize = limit
            )
            val messages = messageDTOs.map { messageDTO -> messageDTO.toMessage(currentUserId) }
            val messageEntities = messages.map { message -> message.toMessageEntity() }
            messageLocalDataSource.upsertMessages(messageEntities)
            messages
        }
    }
}
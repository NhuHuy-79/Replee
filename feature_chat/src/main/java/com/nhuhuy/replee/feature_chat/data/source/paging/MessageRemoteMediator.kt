package com.nhuhuy.replee.feature_chat.data.source.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.file_path.MessageWithLocalPath
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.core.database.entity.pager.MessageRemoteKey
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
    private val messageIdToJump: String?,
    private val conversationId: String,
    private val coreDatabase: CoreDatabase,
    private val pagingMessageNetworkDataSource: PagingMessageNetworkDataSource
) : RemoteMediator<Int, MessageWithLocalPath>() {
    private val messageDao = coreDatabase.provideMessageDao()
    private val remoteKeyDao = coreDatabase.provideMessageRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageWithLocalPath>
    ): MediatorResult {
        return try {
            Timber.d("CurrentThread: ${Thread.currentThread().name}")
            val currentRemoteKey = remoteKeyDao.get(conversationId = conversationId)
            val pageSize = state.config.pageSize.toLong()
            val initialSize = state.config.initialLoadSize.toLong()

            val messageDTOList = when (loadType) {
                LoadType.REFRESH -> {
                    if (messageIdToJump == null) {
                        pagingMessageNetworkDataSource.fetchInitialMessageList(
                            conversationId = conversationId,
                            pageSize = initialSize
                        )
                    } else {
                        pagingMessageNetworkDataSource.fetchMessageListAroundAnchor(
                            conversationId = conversationId,
                            messageId = messageIdToJump,
                            pageSize = initialSize
                        )
                    }
                }

                LoadType.PREPEND -> { //Scroll down
                    Timber.d("Mediator: PREPEND")
                    if (currentRemoteKey == null) return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    if (currentRemoteKey.startReached) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        pagingMessageNetworkDataSource.fetchMessageListAfterAnchor(
                            conversationId = conversationId,
                            messageId = currentRemoteKey.afterMessageId,
                            pageSize = pageSize
                        )
                    }
                }

                LoadType.APPEND -> { //Scroll up
                    Timber.d("Mediator: APPEND")
                    if (currentRemoteKey == null || currentRemoteKey.endReached) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        pagingMessageNetworkDataSource.fetchMessageListBeforeAnchor(
                            conversationId = conversationId,
                            messageId = currentRemoteKey.beforeMessageId,
                            pageSize = pageSize
                        )
                    }
                }
            }

            val messageEntities: List<MessageEntity> = messageDTOList.map { messageDTO ->
                messageDTO.toMessage().toMessageEntity()
            }

            val isDataEmpty = messageEntities.isEmpty()

            val endReached = when (loadType) {
                LoadType.APPEND -> isDataEmpty || messageDTOList.size < pageSize
                LoadType.REFRESH -> if (messageIdToJump != null) false else isDataEmpty || messageDTOList.size < initialSize
                else -> currentRemoteKey?.endReached ?: false
            }

            val startReached = when (loadType) {
                LoadType.PREPEND -> isDataEmpty || messageDTOList.size < pageSize
                LoadType.REFRESH -> {
                    messageIdToJump == null
                }

                else -> currentRemoteKey?.startReached ?: false
            }

            return coreDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Timber.d("Mediator: Hard clearing for Jump")
                    messageDao.clearByConversationId(conversationId)
                    remoteKeyDao.clear(conversationId)
                }

                messageDao.upsertAndDeleteMessages(
                    networkMessages = messageEntities,
                    deleteIds = emptyList()
                )

                val batchOldestId = messageEntities.lastOrNull()?.messageId
                val batchNewestId = messageEntities.firstOrNull()?.messageId

                remoteKeyDao.upsert(
                    MessageRemoteKey(
                        conversationId = conversationId,
                        beforeMessageId = when (loadType) {
                            LoadType.APPEND -> batchOldestId
                                ?: currentRemoteKey?.beforeMessageId.orEmpty()

                            LoadType.PREPEND -> currentRemoteKey?.beforeMessageId.orEmpty()
                            LoadType.REFRESH -> batchOldestId.orEmpty()
                        },
                        afterMessageId = when (loadType) {
                            LoadType.PREPEND -> batchNewestId
                                ?: currentRemoteKey?.afterMessageId.orEmpty()

                            LoadType.APPEND -> currentRemoteKey?.afterMessageId.orEmpty()
                            LoadType.REFRESH -> batchNewestId.orEmpty()
                        },
                        endReached = endReached,
                        startReached = startReached
                    )
                )

                val endOfPaginationReached = when (loadType) {
                    LoadType.APPEND -> endReached
                    LoadType.PREPEND -> startReached
                    LoadType.REFRESH -> {
                        if (messageIdToJump != null) false else (endReached && startReached)
                    }
                }
                val currentMessageCount: Int = messageDao.countMessagesInRoom(conversationId)
                Timber.d("Current Message Size: $currentMessageCount")

                Timber.d("In $loadType, startReached: $startReached, endReached: $endReached, fetch ${messageDTOList.size} messages, return endOfPagination: $endOfPaginationReached")
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}
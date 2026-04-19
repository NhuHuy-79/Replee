package com.nhuhuy.replee.feature_chat.data.source.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.file_path.MessageWithLocalPath
import com.nhuhuy.replee.core.database.entity.pager.MessageRemoteKey
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class LocalPathMessageRemoteMediator(
    private val anchorTimestamp: Long? = null,
    private val anchorMessageId: String? = null,
    private val conversationId: String,
    private val coreDatabase: CoreDatabase,
    private val messageNetworkDataSource: MessageNetworkDataSource,
) : RemoteMediator<Int, MessageWithLocalPath>() {

    private val messageDao = coreDatabase.provideMessageDao()
    private val messageKeyDao = coreDatabase.provideMessageRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageWithLocalPath>
    ): MediatorResult {
        Timber.d("Mediator: START LOAD - Type: $loadType, Anchor: $anchorTimestamp")
        return try {
            val remoteKey = messageKeyDao.get(conversationId)

            // If is first time, fetch by initialLoadSize else pageSize.
            val limit =
                if (loadType == LoadType.REFRESH) state.config.initialLoadSize else state.config.pageSize

            val remoteMessages = when (loadType) {
                LoadType.REFRESH -> {
                    if (anchorTimestamp != null) {
                        messageNetworkDataSource.fetchMessagesAroundAnchor(
                            conversationId = conversationId,
                            anchorTimestamp = anchorTimestamp,
                            limit = limit,
                            anchorMessageId = anchorMessageId
                        )
                    } else {
                        messageNetworkDataSource.fetchMessagesPage(
                            conversationId = conversationId,
                            limit = limit,
                            startAfterCreatedAt = null,
                            startAfterMessageId = null
                        )
                    }
                }

                LoadType.APPEND -> {
                    // If this is end of Paging, stop loading more.
                    if (remoteKey?.endReached == true) return MediatorResult.Success(true)
                    Timber.d("APPEND: Loading messages older than ${remoteKey?.oldestCreatedAt}")
                    //else fetch next page
                    messageNetworkDataSource.fetchMessagesPage(
                        conversationId = conversationId,
                        limit = limit,
                        startAfterCreatedAt = remoteKey?.oldestCreatedAt,
                        startAfterMessageId = remoteKey?.oldestMessageId
                    )
                }

                LoadType.PREPEND -> {
                    // When user need to scroll more below in chat screen, if this is firstTime or early message is downloaded,
                    //then return success
                    if (anchorTimestamp == null || remoteKey?.startReached == true) {
                        return MediatorResult.Success(true)
                    }

                    // else fetch Newest Message
                    messageNetworkDataSource.fetchNewerMessagesPage(
                        conversationId = conversationId,
                        limit = limit,
                        startAfterCreatedAt = remoteKey?.newestCreatedAt,
                        startAfterMessageId = remoteKey?.newestMessageId
                    )
                }
            }

            Timber.d("Mediator: Fetched ${remoteMessages.size} messages")

            val isDataEmpty = remoteMessages.isEmpty()

            val endReached = when (loadType) {
                LoadType.APPEND -> isDataEmpty || remoteMessages.size < limit
                LoadType.REFRESH -> if (anchorTimestamp != null) false else isDataEmpty || remoteMessages.size < limit
                else -> remoteKey?.endReached ?: false
            }

            val startReached = when (loadType) {
                LoadType.PREPEND -> isDataEmpty
                LoadType.REFRESH -> {
                    anchorTimestamp == null
                }

                else -> remoteKey?.startReached ?: false
            }
            val dTOs = remoteMessages.map { it.toMessage().toMessageEntity() }

            coreDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    if (anchorTimestamp != null) {
                        Timber.d("Mediator: Hard clearing for Jump")
                        messageDao.clearByConversationId(conversationId)
                        messageKeyDao.clear(conversationId)
                    } else {
                        // Logic kiểm tra GAP thông thường của bạn
                        val localLatest = messageDao.getLatestMessage(conversationId)
                        val isGap = localLatest != null && (dTOs.firstOrNull()?.sentAt
                            ?: 0) > (localLatest.sentAt ?: 0)
                        if (isGap) {
                            messageDao.clearByConversationId(conversationId)
                            messageKeyDao.clear(conversationId)
                        }
                    }
                }

                messageDao.upsertAndDeleteMessages(networkMessages = dTOs, deleteIds = emptyList())

                val currentKey = messageKeyDao.get(conversationId)
                val oldest = dTOs.lastOrNull()
                val newest = dTOs.firstOrNull()

                messageKeyDao.upsert(
                    MessageRemoteKey(
                        conversationId = conversationId,
                        oldestCreatedAt = oldest?.sentAt ?: currentKey?.oldestCreatedAt,
                        oldestMessageId = oldest?.messageId ?: currentKey?.oldestMessageId,
                        newestCreatedAt = newest?.sentAt ?: currentKey?.newestCreatedAt,
                        newestMessageId = newest?.messageId ?: currentKey?.newestMessageId,
                        endReached = endReached,
                        startReached = startReached
                    )
                )
            }

            val resultEndOfPagination = when (loadType) {
                LoadType.APPEND -> endReached
                LoadType.PREPEND -> startReached
                LoadType.REFRESH -> false
            }

            MediatorResult.Success(endOfPaginationReached = resultEndOfPagination)

        } catch (e: Exception) {
            Timber.e(e, "Mediator: Error")
            MediatorResult.Error(e)
        }
    }
}

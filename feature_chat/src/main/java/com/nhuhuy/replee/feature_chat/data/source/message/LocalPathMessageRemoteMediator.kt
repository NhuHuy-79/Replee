package com.nhuhuy.replee.feature_chat.data.source.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.file_path.MessageWithLocalPath
import com.nhuhuy.replee.core.database.entity.pager.MessageRemoteKey
import com.nhuhuy.replee.core.network.utils.toMilliseconds
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class LocalPathMessageRemoteMediator(
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
        return try {
            val loadSize = when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                LoadType.APPEND -> state.config.pageSize
                LoadType.PREPEND -> return MediatorResult.Success(true)
            }

            val remoteKey = messageKeyDao.get(conversationId)

            Timber.d("Paging config pageSize=${state.config.pageSize}, initialLoadSize=${state.config.initialLoadSize}")
            Timber.d("Remote Key: $remoteKey")

            val startAfterCreatedAt = when (loadType) {
                LoadType.APPEND -> remoteKey?.oldestCreatedAt
                else -> null
            }

            val startAfterMessageId = when (loadType) {
                LoadType.APPEND -> remoteKey?.oldestMessageId
                else -> null
            }

            Timber.d("cursor startAfterCreatedAt=$startAfterCreatedAt startAfterMessageId=$startAfterMessageId")

            if (loadType == LoadType.APPEND && remoteKey?.endReached == true) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val remoteMessages = messageNetworkDataSource.fetchMessagesPage(
                conversationId = conversationId,
                limit = loadSize,
                startAfterCreatedAt = startAfterCreatedAt,
                startAfterMessageId = startAfterMessageId
            )
            Timber.d("remoteMessages size=${remoteMessages.size}")

            val endReached = remoteMessages.size < loadSize

            val entities = remoteMessages.map { dto ->
                dto.toMessage().toMessageEntity()
            }

            val last = remoteMessages.lastOrNull()

            Timber.d("endReached=$endReached")
            Timber.d("entities: $entities")
            Timber.d("newCursor oldestCreatedAt=${last?.sendAt}, oldestMessageId=${last?.messageId}")

            coreDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    messageKeyDao.clear(conversationId)
                }

                messageDao.upsertAll(entities)

                messageKeyDao.upsert(
                    MessageRemoteKey(
                        conversationId = conversationId,
                        oldestCreatedAt = last?.sendAt?.toMilliseconds(),
                        oldestMessageId = last?.messageId,
                        endReached = endReached
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endReached)

        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }

}
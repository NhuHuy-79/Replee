package com.nhuhuy.replee.feature_chat.data.source.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.message.MessageEntity
import com.nhuhuy.replee.core.database.entity.pager.MessageRemoteKey
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
    private val conversationId: String,
    private val db: CoreDatabase,
    private val network: MessageNetworkDataSource
) : RemoteMediator<Int, MessageEntity>() {

    val messageDao = db.provideMessageDao()
    val remoteKeyDao = db.provideMessageRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageEntity>
    ): MediatorResult {
        return try {

            val loadSize = when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                LoadType.APPEND -> state.config.pageSize
                LoadType.PREPEND -> return MediatorResult.Success(true)
            }

            val remoteKey = remoteKeyDao.get(conversationId)
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

            val remoteMessages = network.fetchMessagesPage(
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

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clear(conversationId)
                }

                messageDao.upsertAll(entities)

                remoteKeyDao.upsert(
                    MessageRemoteKey(
                        conversationId = conversationId,
                        oldestCreatedAt = last?.sendAt,
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
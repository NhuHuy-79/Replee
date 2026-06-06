package com.nhuhuy.replee.core.data.paging_source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.APPEND
import androidx.paging.LoadType.PREPEND
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.file_path.MessageWithLocalPath
import com.nhuhuy.replee.core.database.mapper.toMessageEntity
import com.nhuhuy.replee.core.model.chat.MessageStatus
import com.nhuhuy.replee.core.network.data_source.message.PagingMessageNetworkDataSource
import com.nhuhuy.replee.core.network.mapper.toMessage
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@OptIn(ExperimentalPagingApi::class)
class LocalPathMessageRemoteMediator(
    private val currentUserId: String?,
    private val conversationId: String,
    private val anchorMessageId: String? = null,
    private val coreDatabase: CoreDatabase,
    private val pagingMessageNetworkDataSource: PagingMessageNetworkDataSource,
) : RemoteMediator<Int, MessageWithLocalPath>() {

    private var recentLoadTime = 0L
    private val delay = 5.seconds.toLong(DurationUnit.MILLISECONDS)

    private val messageDao = coreDatabase.provideMessageDao()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageWithLocalPath>
    ): MediatorResult {
        return try {
            val pageSize = state.config.pageSize.toLong()
            val initialSize = state.config.initialLoadSize.toLong()

            val messageDTOs = when (loadType) {
                REFRESH -> {
                    if (anchorMessageId == null) {
                        pagingMessageNetworkDataSource.fetchInitialMessageList(
                            conversationId = conversationId,
                            pageSize = initialSize
                        )
                    } else {
                        pagingMessageNetworkDataSource.fetchMessageListAroundAnchor(
                            conversationId = conversationId,
                            messageId = anchorMessageId,
                            pageSize = pageSize
                        )
                    }
                }

                PREPEND -> {
                    val newestItem = state.pages.flatMap { it.data }
                        .firstOrNull { it.message.status == MessageStatus.SYNCED.name }
                    if (newestItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        pagingMessageNetworkDataSource.fetchMessageListAfterAnchor(
                            conversationId = conversationId,
                            messageId = newestItem.message.messageId,
                            pageSize = pageSize
                        )
                    }
                }

                APPEND -> {
                    val oldestItem = state.pages.flatMap { it.data }
                        .lastOrNull { it.message.status == MessageStatus.SYNCED.name }
                    if (oldestItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        pagingMessageNetworkDataSource.fetchMessageListBeforeAnchor(
                            conversationId = conversationId,
                            messageId = oldestItem.message.messageId,
                            pageSize = pageSize
                        )
                    }
                }
            }

            val messageEntities = messageDTOs.map { messageDTO ->
                messageDTO.toMessage(currentUserId).toMessageEntity()
            }

            coreDatabase.withTransaction {
                if (loadType == REFRESH) {
                    messageDao.clearByConversationId(conversationId)
                }
                messageDao.upsertAll(messageEntities)
            }

            val endOfPaginationReached = when (loadType) {
                REFRESH -> messageDTOs.isEmpty() || messageDTOs.size < initialSize
                PREPEND, APPEND -> messageDTOs.isEmpty() || messageDTOs.size < pageSize
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }
}
package com.nhuhuy.replee.core.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.network.data_source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.message.MessageNetworkDataSource
import com.nhuhuy.replee.core.network.mapper.toMessage
import timber.log.Timber

class SearchedMessagePagingSource(
    private val searchQuery: String,
    private val conversationId: String,
    private val currentUserId: String,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource
) : PagingSource<String, Message>() {
    override fun getRefreshKey(state: PagingState<String, Message>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Message> {
        return try {
            if (searchQuery.isBlank()) return LoadResult.Page(
                nextKey = null,
                prevKey = null,
                data = emptyList()
            )

            val currentKey: String? = params.key
            val afterTimestamp = conversationNetworkDataSource.fetchConversationById(conversationId)
                ?.lastTimeDeleted[currentUserId]
            val pageSize = params.loadSize
            val messageDTOs = messageNetworkDataSource.fetchMessageByQuery(
                query = searchQuery,
                conversationId = conversationId,
                currentUserId = currentUserId,
                lastMessageId = currentKey.orEmpty(),
                limit = pageSize,
                afterTimestamp = afterTimestamp
            )

            val data = messageDTOs.map { messageDTO ->
                messageDTO.toMessage(currentUserId = currentUserId)
            }

            val prevKey = null
            val nextKey = if (data.isEmpty() || data.size < pageSize) {
                null
            } else {
                data.last().messageId
            }

            Timber.d("Searched Message: ${data.size}")

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }
}
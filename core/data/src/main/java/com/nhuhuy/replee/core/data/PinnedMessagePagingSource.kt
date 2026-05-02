package com.nhuhuy.replee.core.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.network.data_source.MessageNetworkDataSource
import com.nhuhuy.replee.core.network.mapper.toMessage
import timber.log.Timber

class PinnedMessagePagingSource(
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationId: String,
    private val currentUserId: String
) : PagingSource<String, Message>() {
    override fun getRefreshKey(state: PagingState<String, Message>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Message> {
        return try {
            val currentKey: String? = params.key
            val pageSize = params.loadSize
            val messageDTOs = messageNetworkDataSource.fetchPinnedMessageBefore(
                conversationId = conversationId,
                currentUserId = currentUserId,
                lastMessageId = currentKey.orEmpty(),
                limit = pageSize
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

            Timber.d("Pinned Message: ${data.size}")

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
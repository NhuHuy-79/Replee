package com.nhuhuy.replee.feature_chat.data.repository.metadata

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.feature_chat.data.source.metadata.MetaDataNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.repository.MetaDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class MetaDataRepositoryImp @Inject constructor(
    private val sessionManager: SessionManager,
    private val metaDataNetworkDataSource: MetaDataNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : MetaDataRepository {
    override suspend fun updateMyTyping(
        conversationId: String,
        userId: String,
        typing: Boolean
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            metaDataNetworkDataSource.setTyping(
                conversationId = conversationId,
                userId = userId,
                typing = typing
            )
        }
    }

    override suspend fun clearTyping(conversationId: String): NetworkResult<Unit> {
        TODO("Not yet implemented")
    }

    override fun getOtherTyping(conversationId: String): Flow<List<String>> {
        val currentUserId = sessionManager.getUserIdOrNull()
        return metaDataNetworkDataSource.observeTyping(conversationId).mapNotNull { typing ->
            typing.filter { entry -> entry.value && entry.key != currentUserId }
                .keys
                .toList()
        }.flowOn(ioDispatcher)
    }

    override suspend fun updateMyReading(
        conversationId: String,
        userId: String,
        reading: Long
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            metaDataNetworkDataSource.setLastReadingTime(
                conversationId = conversationId,
                userId = userId,
                reading = reading
            )
        }
    }

    override fun getOtherReading(conversationId: String, otherUserId: String): Flow<Long> {
        return metaDataNetworkDataSource.observeLastReadingTime(conversationId).filter { reading ->
            reading.containsKey(otherUserId)
        }.map { reading ->
            reading[otherUserId] ?: 0L
        }.flowOn(ioDispatcher)
    }
}
package com.nhuhuy.replee.feature_chat.data.repository.metadata

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.replee.feature_chat.data.source.metadata.MetaDataNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.repository.MetaDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
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
    ) {
        return withContext(ioDispatcher) {
            metaDataNetworkDataSource.setTyping(
                conversationId = conversationId,
                userId = userId,
                typing = typing
            )
        }
    }

    override fun getOtherTyping(conversationId: String): Flow<List<String>> {
        val currentUserId = sessionManager.getUserIdOrNull()
        return metaDataNetworkDataSource.observeTyping(conversationId).mapNotNull { typing ->
            typing.filter { entry -> entry.value && entry.key != currentUserId }
                .keys
                .toList()
        }.flowOn(ioDispatcher)
    }
}
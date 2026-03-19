package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.mapper.toAccountEntity
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.common.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.mapNotNullData
import com.nhuhuy.replee.feature_chat.data.mapper.createConversationDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toConversation
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationEntity
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ConversationRepositoryImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val sessionManager: SessionManager,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
) : ConversationRepository {
    override fun listenConversationWithLimit(
        limit: Int,
        ownerId: String
    ): Flow<List<DataChange<Conversation>>> {
        return conversationNetworkDataSource.listenConversationChanges(
            ownerId = ownerId,
            limit = limit
        ).map { dataChanges ->
            Timber.d("Data Change: ${dataChanges.size}")
            dataChanges.mapNotNull { dataChange ->
                dataChange.mapNotNullData { dto ->
                    dto.toConversation(ownerId)
                }
            }
        }
    }

    override suspend fun fetchOtherUserInConversations(ownerId: String) {
        return withContext(ioDispatcher) {
            val uids = conversationLocalDataSource.getOtherUserInConversation(ownerId)

            if (uids.isEmpty()) {
                Timber.e("List is empty")
                return@withContext
            }

            Timber.d("Fetched User list: $uids")

            val accounts = accountNetworkDataSource.fetchAccountByIdList(uids).map { accountDTO ->
                accountDTO.toAccountEntity()
            }
            accountLocalDataSource.upsertAccounts(accounts)
        }
    }

    override suspend fun fetchConversations(): NetworkResult<List<Conversation>> {
        return execute {
            val uid = sessionManager.requireUserId()
            conversationNetworkDataSource.fetchConversationsByUser(uid)
                .mapNotNull { conversationDTO ->
                conversationDTO.toConversation(uid)
            }
        }
    }

    override fun observeLocalConversations(ownerId: String): Flow<List<Conversation>> {
        return conversationLocalDataSource.observeConversationAndUsers(ownerId).map { entities ->
            entities.map { entity ->
                Timber.d("${entity.toConversation()}")
                entity.toConversation()
            }
                .filter { conversation ->
                    conversation.lastMessageContent.isNotBlank()

                }
        }
            .flowOn(ioDispatcher)
    }

    override suspend fun saveConversations(conversations: List<Conversation>) {
        val entities = conversations.map { conversation ->
            conversation.toConversationEntity()
        }
        conversationLocalDataSource.upsertConversations(entities)
    }

    override fun observeConversationById(conversationId: String): Flow<Conversation> {
        return conversationLocalDataSource.observeConversationById(conversationId)
            .map { conversation ->
                Timber.d("$conversation")
                conversation?.toConversation() ?: Conversation()
            }
    }

    override suspend fun getOrCreateConversation(
        ownerId: String,
        otherUserId: String
    ): NetworkResult<String> {
        return executeWithTimeout {
            val entity = conversationLocalDataSource.getConversationAndUserById(
                ownerId = ownerId,
                otherUserId = otherUserId
            )
            val dto = entity.createConversationDTO()
            conversationNetworkDataSource.sendConversation(dto)
            entity.conversation.id

        }
    }

    override suspend fun updateLocalDataChange(
        dataChanges: List<DataChange<Conversation>>
    ): NetworkResult<Unit> = execute {
        val currentUserId: String = sessionManager.requireUserId()
        val upserts = mutableListOf<Conversation>()
        val deletes = mutableListOf<String>()

        for (change in dataChanges) {
            when (change) {
                is DataChange.Delete -> deletes.add(change.id)
                is DataChange.Upsert -> upserts.add(change.data)
                DataChange.Empty -> conversationLocalDataSource.deleteConversationsByUid(
                    currentUserId
                )
            }
        }

        val mappedUpsert = upserts.map { conversation -> conversation.toConversationEntity() }
        conversationLocalDataSource.upsertAndDeleteConversations(
            upsert = mappedUpsert,
            delete = deletes,
        )

    }

    override suspend fun updateMetadataConversation(message: Message): NetworkResult<Unit> {
        return execute {
            val messageEntity = message.toMessageEntity()
            conversationLocalDataSource.updateLastMessage(messageEntity)

            val conversationDTO = conversationNetworkDataSource
                .fetchConversationByIdOrThrow(message.conversationId)
            val messageDTO = message.toMessageDTO()

            conversationNetworkDataSource.updateLastMessage(
                message = messageDTO,
                conversation = conversationDTO
            )
        }
    }

    override fun observeOtherUserInConversation(currentUserId: String): Flow<List<String>> {
        return conversationLocalDataSource.observeOtherUserInConversation(currentUserId)
    }

    override suspend fun markAllMessagesRead(
        conversationId: String,
        currentUserId: String
    ): NetworkResult<String> {
        return execute {
            conversationLocalDataSource.clearUnreadMessages(conversationId)
            conversationNetworkDataSource.deleteAllUnreadMessages(conversationId, currentUserId)
            conversationId
        }
    }

}
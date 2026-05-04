package com.nhuhuy.replee.core.data.repository.chat

import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.data.mapper.createConversationDTO
import com.nhuhuy.replee.core.data.mapper.toAccountEntity
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.database.data_source.ConversationLocalDataSource
import com.nhuhuy.replee.core.database.mapper.toConversation
import com.nhuhuy.replee.core.database.mapper.toConversationEntity
import com.nhuhuy.replee.core.database.mapper.toMessageEntity
import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.network.data_source.account.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.core.network.mapper.toMessageDTO
import com.nhuhuy.replee.core.network.model.DataChange
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import com.nhuhuy.replee.core.network.mapper.toConversation as toConversationNetwork

class ConversationRepositoryImp @Inject constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val sessionManager: SessionManager,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
) : ConversationRepository {
    override fun listenConversationWithLimit(
        limit: Int,
        ownerId: String
    ): Flow<Unit> {
        return conversationNetworkDataSource.listenConversationChanges(
            ownerId = ownerId,
            limit = limit
        )
            .distinctUntilChanged()
            .map { dataChanges ->
                val deletes = mutableListOf<String>()
                val upserts = mutableListOf<Conversation?>()

                for (change in dataChanges) {
                    when (change) {
                        is DataChange.Delete -> deletes.add(change.id)
                        is DataChange.Upsert -> upserts.add(change.data.toConversationNetwork(ownerId))
                    }
                }

                val mappedUpsert =
                    upserts.mapNotNull { conversation -> conversation?.toConversationEntity() }
                conversationLocalDataSource.upsertAndDeleteConversations(
                    upsert = mappedUpsert,
                    delete = deletes,
                )

                Timber.d("Listen Conversation: ${dataChanges.size}")
            }.flowOn(ioDispatcher)
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
                    conversationDTO.toConversationNetwork(uid)
                }
        }
    }

    override fun observeLocalConversationList(ownerId: String): Flow<List<Conversation>> {
        return conversationLocalDataSource.observeConversationAndUsers(ownerId)
            .map { entities ->
                Timber.d("Entities: ${entities.size}")
                entities.map { entity -> entity.toConversation() }
                    .filterNot { conversation ->
                        conversation.lastMessageType == MessageType.TEXT &&
                                conversation.lastMessageContent.isBlank()

                    }
            }
            .distinctUntilChanged()
            .flowOn(ioDispatcher)
    }

    override suspend fun saveConversations(conversations: List<Conversation>) {
        val entities = conversations.map { conversation ->
            conversation.toConversationEntity()
        }
        conversationLocalDataSource.upsertConversations(entities)
    }

    override suspend fun getOrCreateConversation(
        ownerId: String,
        otherUserId: String
    ): NetworkResult<String> {
        return execute(ioDispatcher) {
            val entity = conversationLocalDataSource.getConversationAndUserById(
                ownerId = ownerId,
                otherUserId = otherUserId
            )
            val dto = entity.createConversationDTO()
            conversationNetworkDataSource.sendConversation(dto)
            entity.conversation.id

        }
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

    override suspend fun getConversationById(conversationId: String): Conversation {
        return withContext(ioDispatcher) {
            conversationLocalDataSource.getConversationById(conversationId)?.toConversation()
                ?: Conversation()
        }
    }

    override fun observeOtherUserInConversation(currentUserId: String): Flow<List<String>> {
        return conversationLocalDataSource.observeOtherUserInConversation(currentUserId)
    }

    override suspend fun markAllMessagesRead(
        conversationId: String,
        currentUserId: String
    ): NetworkResult<Unit> {
        return executeWithTimeout(ioDispatcher) {
            Timber.d("Delete unread messages")
            conversationLocalDataSource.clearUnreadMessages(conversationId)
            conversationNetworkDataSource.deleteAllUnreadMessages(conversationId, currentUserId)
        }
    }

    override suspend fun deleteMetadataLastMessage(message: Message): NetworkResult<String> {
        return executeWithTimeout(ioDispatcher) {
            val conversation =
                conversationNetworkDataSource.fetchConversationByIdOrThrow(message.conversationId)
            if (conversation.lastMessageId == message.messageId) {
                //updateLastMessage
            }
            //Compare lastMessageId in Conversation and MessageId.
            //Delete lastMessage metadata and replace by lastClose Message.
            TODO("Implemenet logic here")

            message.conversationId

        }
    }

    override suspend fun deleteConversation(id: String): NetworkResult<Unit> {
        return execute {
            val uid = sessionManager.requireUserId()
            val now = System.currentTimeMillis()
            conversationLocalDataSource.updateDeleteAndTimestamp(id, true, now)

            val patch = mapOf(
                "id" to id,
                "isDeleted.$uid" to true,
                "lastTimeDeleted.$uid" to now
            )
            conversationNetworkDataSource.updateConversationDataMap(listOf(patch))
        }
    }

    override suspend fun deleteMultipleConversations(ids: List<String>): NetworkResult<Unit> {
        return execute {
            val uid = sessionManager.requireUserId()
            val now = System.currentTimeMillis()
            val patches = mutableListOf<Map<String, Any>>()

            ids.forEach { id ->
                conversationLocalDataSource.updateDeleteAndTimestamp(id, true, now)
                patches.add(
                    mapOf(
                        "id" to id,
                        "isDeleted.$uid" to true,
                        "lastTimeDeleted.$uid" to now
                    )
                )
            }

            if (patches.isNotEmpty()) {
                conversationNetworkDataSource.updateIsDeletedMultiConversations(patches)
            }
        }
    }

}

package com.nhuhuy.replee.feature_chat.data.repository.conversation

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.mapper.toAccountEntity
import com.nhuhuy.replee.core.data.utils.ApplicationCoroutineScope
import com.nhuhuy.replee.core.data.utils.IoDispatcher
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.data.mapper.createConversationDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toConversation
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationEntity
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.converastion.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageType
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ConversationRepositoryImp @Inject constructor(
    @ApplicationCoroutineScope private val externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
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
                        is DataChange.Upsert -> upserts.add(change.data.toConversation(ownerId))
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
                    conversationDTO.toConversation(uid)
                }
        }
    }

    override fun observeLocalConversationList(ownerId: String): Flow<List<Conversation>> {
        return conversationLocalDataSource.observeConversationAndUsers(ownerId)
            .distinctUntilChanged()
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
    ): NetworkResult<String> {
        return executeWithTimeout(ioDispatcher) {
            conversationLocalDataSource.clearUnreadMessages(conversationId)
            conversationNetworkDataSource.deleteAllUnreadMessages(conversationId, currentUserId)
            conversationId
        }
    }

    override suspend fun deleteMetadataLastMessage(message: Message): NetworkResult<String> {
        return executeWithTimeout(ioDispatcher) {
            val conversation =
                conversationNetworkDataSource.fetchConversationByIdOrThrow(message.conversationId)
            if (conversation.lastMessageId == message.messageId) {
                //updateLastMessage
            }
            //Compare lastMessageId in Conversation and Message Id.
            //Delete lastMessage metadata and replace by lastClose Message.
            TODO("Implemenet logic here")

            message.conversationId

        }
    }

}
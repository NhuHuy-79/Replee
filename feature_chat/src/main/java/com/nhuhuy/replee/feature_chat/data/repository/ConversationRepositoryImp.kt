package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.common.mapper.toAccountEntity
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.core.network.model.mapData
import com.nhuhuy.replee.feature_chat.data.mapper.toConversation
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ConversationRepositoryImp @Inject constructor(
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : ConversationRepository, NetworkResultCaller(ioDispatcher, logger) {
    override suspend fun fetchOtherUserInConversations(ownerId: String) {
        return withContext(ioDispatcher) {
            val uids = conversationNetworkDataSource.getConversationUserIdsWithOwner(ownerId)

            if (uids.isEmpty()) return@withContext

            val accounts = accountNetworkDataSource.fetchAccountByIdList(uids).map { accountDTO ->
                accountDTO.toAccountEntity()
            }
            accountLocalDataSource.upsertAccounts(accounts)
        }
    }

    override suspend fun fetchConversations(): NetworkResult<List<Conversation>> {
        return safeCallWithTimeout {
            val uid = firebaseAuthEmailService.getCurrentUser().uid
            conversationNetworkDataSource.fetchConversationsByUser(uid).map { conversationDTO ->
                conversationDTO.toConversation(uid)
            }
        }
    }

    override fun observeLocalConversations(): Flow<List<Conversation>> {
        val uid = firebaseAuthEmailService.getCurrentUser().uid
        return conversationLocalDataSource.observeConversationAndUsers(uid).map { entities ->
            entities.map { entity ->
                Timber.d("${entity.toConversation()}")
                entity.toConversation()
            }.filter { conversation ->
                conversation.lastMessageContent.isNotEmpty()
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

    override fun observeNetworkConversation(): Flow<NetworkResult<List<Conversation>>> {
        val uid = firebaseAuthEmailService.getCurrentUser().uid
        return conversationNetworkDataSource.streamConversationsByOwner(uid)
            .map { conversationDTOS ->
                val data =
                    conversationDTOS.map { conversationDTO -> conversationDTO.toConversation(uid) }
                NetworkResult.Success(data) as NetworkResult<List<Conversation>>
            }.catch { exception -> emit(NetworkResult.Failure(exception)) }
    }

    override suspend fun getOrCreateConversation(otherUser: Account): NetworkResult<String> {
        return safeCallWithTimeout {
            val currentUserId = firebaseAuthEmailService.getCurrentUser().uid
            val entity = conversationLocalDataSource.getConversationAndUserById(
                ownerId = currentUserId,
                otherUserId = otherUser.id
            )
            val dto = entity.toConversationDTO()
            conversationNetworkDataSource.sendConversation(dto)
            entity.conversation.id
        }
    }

    override suspend fun getOrCreateConversation(
        ownerId: String,
        otherUserId: String
    ): NetworkResult<String> {
        return safeCallWithTimeout {
            val entity = conversationLocalDataSource.getConversationAndUserById(
                ownerId = ownerId,
                otherUserId = otherUserId
            )
            val dto = entity.toConversationDTO()
            conversationNetworkDataSource.sendConversation(dto)
            entity.conversation.id

        }
    }

    override fun observeNetworkConversationChange(
        ownerId: String
    ): Flow<List<DataChange<Conversation>>> {
        return conversationNetworkDataSource
            .listenConversationChangesByOwner(ownerId)
            .map { dataChanges ->
                Timber.d("Data Change: $dataChanges")
                dataChanges.map { dataChange ->
                    dataChange.mapData { dto ->
                        dto.toConversation(ownerId)
                    }
                }
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun updateLocalDataChange(
        dataChanges: List<DataChange<Conversation>>
    ) {

        val upserts = mutableListOf<Conversation>()
        val deletes = mutableListOf<String>()

        for (change in dataChanges) {
            when (change) {
                is DataChange.Delete -> deletes.add(change.id)
                is DataChange.Upsert -> upserts.add(change.data)
            }
        }

        if (upserts.isEmpty() && deletes.isEmpty()) {
            Timber.d("$upserts - $deletes")
            return
        }
        val mappedUpsert = upserts.map { conversation -> conversation.toConversationEntity() }
        conversationLocalDataSource.upsertAndDeleteConversations(
            upsert = mappedUpsert,
            delete = deletes
        )
    }

}
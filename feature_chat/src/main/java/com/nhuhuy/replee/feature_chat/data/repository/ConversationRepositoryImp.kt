package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.data.model.toAccountEntity
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.mapResource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.feature_chat.data.mapper.toConversation
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationEntity
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ConversationRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : ConversationRepository {
    override suspend fun fetchOtherUserInConversations(ownerId: String) {
        return withContext(dispatcher) {
            val uids = conversationNetworkDataSource.getConversationUserIdsWithOwner(ownerId)

            if (uids.isEmpty()) return@withContext

            val accounts = accountNetworkDataSource.fetchAccountByIdList(uids).map { accountDTO ->
                accountDTO.toAccountEntity()
            }
            accountLocalDataSource.upsertAccounts(accounts)
        }
    }

    override suspend fun fetchConversations(): Resource<List<Conversation>, RemoteFailure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ){
                val uid = firebaseAuthEmailService.getCurrentUser().uid
                conversationNetworkDataSource.fetchConversationsByUser(uid).map { conversationDTO ->
                    conversationDTO.toConversation(uid)
                }
            }
        }
    }

    override suspend fun getConversationCount(): Int {
        val uid = firebaseAuthEmailService.getCurrentUser().uid
        return conversationLocalDataSource.getConversationsCount(uid)
    }

    override fun observeLocalConversations(): Flow<List<Conversation>> {
        val uid = firebaseAuthEmailService.getCurrentUser().uid
        return conversationLocalDataSource.observeConversationAndUsers(uid).map { entities ->
            entities.map { entity ->
                Timber.d("${entity.toConversation()}")
                entity.toConversation()
            }
        }.flowOn(dispatcher)
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

    override fun observeNetworkConversations(): Flow<Resource<List<Conversation>, RemoteFailure>> {
        val uid = firebaseAuthEmailService.getCurrentUser().uid
        return conversationNetworkDataSource.streamConversationsByUser(uid).mapResource { conversationDTOS ->
            conversationDTOS.map { conversationDTO ->
                Timber.d("list: $conversationDTO")
                conversationDTO.toConversation(uid)
            }
        }
    }

    override suspend fun getOrCreateConversation(otherUser: Account): Resource<String, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                val currentUserId = firebaseAuthEmailService.getCurrentUser().uid
                val entity = conversationLocalDataSource.getConversationAndUserById(ownerId = currentUserId, otherUserId = otherUser.id)
                try {
                    val dto = entity.toConversationDTO()
                    conversationNetworkDataSource.sendConversation(dto)
                } catch (e: Exception) {
                    Timber.e(e)
                }

                entity.conversation.id
            }
        }
    }

}
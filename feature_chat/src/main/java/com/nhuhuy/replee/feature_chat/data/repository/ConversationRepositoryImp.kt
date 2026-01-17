package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.mapResource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthService
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
    private val firebaseAuthService: FirebaseAuthService,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : ConversationRepository {
    override suspend fun fetchConversationList(): Resource<List<Conversation>, RemoteFailure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ){
                val uid = firebaseAuthService.provideCurrentUser().uid
                conversationNetworkDataSource.getConversationWithUids(uid).map { conversationDTO ->
                    conversationDTO.toConversation(uid)
                }
            }
        }
    }

    override suspend fun getConversationListCount(): Int {
        val uid = firebaseAuthService.provideCurrentUser().uid
        return conversationLocalDataSource.getConversationListCount(uid)
    }

    override fun observeConversationList(): Flow<List<Conversation>> {
        val uid = firebaseAuthService.provideCurrentUser().uid
        return conversationLocalDataSource.observeConversationAndUsers(uid).map { entities ->
            entities.map { entity -> entity.toConversation() }
        }.flowOn(dispatcher)
    }

    override suspend fun saveConversationToLocal(conversations: List<Conversation>) {
        val entities = conversations.map { conversation ->
            conversation.toConversationEntity()
        }
        conversationLocalDataSource.addConversationList(entities)
    }

    override fun listenFromNetwork(): Flow<Resource<List<Conversation>, RemoteFailure>> {
        val uid = firebaseAuthService.provideCurrentUser().uid
        return conversationNetworkDataSource.observeConversationList(uid).mapResource { conversationDTOS ->
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
                val currentUserId = firebaseAuthService.provideCurrentUser().uid
                val entity = conversationLocalDataSource.getOrCreateConversation(ownerId = currentUserId, otherUserId = otherUser.id)
                try {
                    val dto = entity.toConversationDTO()
                    conversationNetworkDataSource.addConversation(dto)
                } catch (e: Exception) {
                    Timber.e(e)
                }

                entity.conversation.id
            }
        }
    }

}
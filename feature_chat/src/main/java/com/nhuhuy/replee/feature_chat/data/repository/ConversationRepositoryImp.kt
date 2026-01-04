package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.mapResource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.error_handling.toRemoteFailure
import com.nhuhuy.replee.core.firebase.AuthDataSource
import com.nhuhuy.replee.feature_chat.data.mapper.ConversationMapper
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationRemoteDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ConversationRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val conversationMapper: ConversationMapper,
    private val authDataSource: AuthDataSource,
    private val conversationRemoteDataSource: ConversationRemoteDataSource,
) : ConversationRepository {

    override fun observeConversationList(): Flow<Resource<List<Conversation>, RemoteFailure>> {
        val ownerId = authDataSource.provideCurrentUser().uid
        return conversationRemoteDataSource.observeConversationList(ownerId).mapResource { list ->
            list.map { conversationDTO ->
                conversationMapper.fromRemoteToDomain(conversationDTO)
            }
        }.flowOn(dispatcher)

    }

    override suspend fun addConversation(conversation: Conversation): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                val conversationDTO = conversationMapper.fromDomainToRemote(conversation)
                conversationRemoteDataSource.addConversation(conversationDTO)
            }
        }
    }
}
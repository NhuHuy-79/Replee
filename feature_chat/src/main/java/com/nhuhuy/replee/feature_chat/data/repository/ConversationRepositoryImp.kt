package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.data.model.toAccount
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.mapResource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthService
import com.nhuhuy.replee.feature_chat.data.mapper.toConversation
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
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
    private val firebaseAuthService: FirebaseAuthService,
    private val accountDataSource: AccountNetworkDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource,
) : ConversationRepository {

    override fun observeConversationList(): Flow<Resource<List<Conversation>, RemoteFailure>> {
        val ownerId = firebaseAuthService.provideCurrentUser().uid
        return conversationNetworkDataSource.observeConversationList(ownerId).mapResource { list ->
            list.map { conversationDTO ->
                conversationDTO.toConversation(ownerId)
            }
        }.flowOn(dispatcher)

    }

    override suspend fun addConversation(otherUser: Account): Resource<String, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                val currentUserId = firebaseAuthService.provideCurrentUser().uid
                val currentAccount = accountDataSource.getAccountById(currentUserId).toAccount()

                conversationNetworkDataSource.createNewConversation(user1 = currentAccount, user2 = otherUser)
            }
        }
    }
}
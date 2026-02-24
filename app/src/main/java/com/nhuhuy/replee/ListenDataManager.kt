package com.nhuhuy.replee

import com.nhuhuy.replee.core.firebase.model.DataChange
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface ListenDataManager {
    val listenableConversationFlow: Flow<List<DataChange<Conversation>>>
    suspend fun updateConversationChange(dataChanges: List<DataChange<Conversation>>)
}


@Singleton
class ListenDataManagerImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val conversationRepository: ConversationRepository,
) : ListenDataManager {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val listenableConversationFlow: Flow<List<DataChange<Conversation>>>
        get() = authRepository.observeAuthState()
            .distinctUntilChanged()
            .flatMapLatest { uid ->
                if (uid == null) {
                    emptyFlow()
                } else {
                    conversationRepository.observeNetworkConversationChange(uid)
                }
            }

    override suspend fun updateConversationChange(dataChanges: List<DataChange<Conversation>>) {
        withContext(ioDispatcher) {
            val upsert = mutableListOf<Conversation>()
            val delete = mutableListOf<String>()

            for (change in dataChanges) {
                when (change) {
                    is DataChange.Upsert -> upsert.add(change.data)
                    is DataChange.Delete -> delete.add(change.id)
                }
            }

            conversationRepository.updateLocalDataChange(
                upsert = upsert,
                delete = delete
            )
        }
    }
}
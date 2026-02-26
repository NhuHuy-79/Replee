package com.nhuhuy.replee.worker

import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

interface ListenConversationsManager {
    fun build(): Flow<Unit>
}


class ListenConversationsManagerImp @Inject constructor(
    private val authRepository: AuthRepository,
    private val conversationRepository: ConversationRepository,
) : ListenConversationsManager {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun build(): Flow<Unit> {
        return authRepository.observeAuthState()
            .distinctUntilChanged()
            .flatMapLatest { uid ->

            Timber.d(uid)
                if (uid == null) emptyFlow()
                else conversationRepository.observeNetworkConversationChange(uid)
            }.onEach { dataChanges ->
                Timber.d("Conversation Change: ${dataChanges.size}")
                conversationRepository.updateLocalDataChange(dataChanges)
            }
            .map { }
    }
}
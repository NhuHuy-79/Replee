package com.nhuhuy.replee.feature_chat.data

import com.nhuhuy.replee.core.common.qualifier.AppCoroutineScope
import com.nhuhuy.replee.core.firebase.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface ConversationListener {
    fun stopListening()
    fun listenToNetworkDataSource(ownerId: String)
}

@Singleton
class ConversationListenerImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    @AppCoroutineScope private val appScope: CoroutineScope,
    private val conversationRepository: ConversationRepository
) : ConversationListener {

    private var syncJob: Job? = null

    override fun stopListening() {
        syncJob?.cancel()
        syncJob = null
    }

    override fun listenToNetworkDataSource(ownerId: String) {
        syncJob?.cancel()

        syncJob = appScope.launch(ioDispatcher) {
            conversationRepository.observeNetworkConversationChange(ownerId)
                .catch { throwable ->
                    Timber.e(throwable)
                }
                .collect { changes ->
                    Timber.d("Collect network data: $changes")
                    val upsert = mutableListOf<Conversation>()
                    val delete = mutableListOf<String>()

                    for (change in changes) {
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

}
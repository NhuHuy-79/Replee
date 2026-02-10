package com.nhuhuy.replee

import com.nhuhuy.replee.core.common.qualifier.AppCoroutineScope
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_chat.data.ConversationListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface ListenDataManager {
    val uidFlow: Flow<String?>
    fun start()
    fun stop()
}

@Singleton
class ListenDataManagerImp @Inject constructor(
    @AppCoroutineScope private val appScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val conversationListener: ConversationListener
) : ListenDataManager {
    override val uidFlow: Flow<String?>
        get() = authRepository.observeAuthState()

    private var listenJob: Job? = null

    override fun start() {
        if (listenJob?.isActive == true) return

        listenJob = appScope.launch(ioDispatcher) {
            uidFlow
                .distinctUntilChanged()
                .catch { throwable -> Timber.e(throwable) }
                .collectLatest { uid ->
                    if (uid == null) {
                        Timber.e("Uid is null, stop listening!")
                        conversationListener.stopListening()
                    } else {
                        Timber.d("Start listening to data source")
                        conversationListener.listenToNetworkDataSource(uid)
                    }
                }
        }
    }

    override fun stop() {
        listenJob?.cancel()
        listenJob = null
        conversationListener.stopListening()
    }

}
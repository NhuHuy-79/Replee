package com.nhuhuy.replee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.repository.PresenceRepository
import com.nhuhuy.replee.core.common.data.data_store.AppDataStore
import com.nhuhuy.replee.core.common.data.data_store.ThemeMode
import com.nhuhuy.replee.core.network.data_source.AuthenticatedState
import com.nhuhuy.replee.core.network.manager.ConnectivityObserver
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.worker.ListenConversationsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(
    connectionObserver: ConnectivityObserver,
    authRepository: AuthRepository,
    appDataStore: AppDataStore,
    private val presenceRepository: PresenceRepository,
    private val listenConversationsManager: ListenConversationsManager,
) : ViewModel(){
    val network = connectionObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = NetworkStatus.Online
        )
    val authenticatedState = authRepository.observeAuthenticationState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthenticatedState.Loading
        )

    init {
        listenToNetworkConversations()
        listenAccountOnlineStatus()
    }

    private fun listenAccountOnlineStatus() {
        viewModelScope.launch {
            authenticatedState.collect { state ->
                when (state) {
                    is AuthenticatedState.Authenticated -> {
                        presenceRepository.setOnline(state.uid)
                        Timber.d("User is online!")
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun listenToNetworkConversations() {
        listenConversationsManager.build()
            .launchIn(viewModelScope)
    }

    val theme = appDataStore.observeTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DEFAULT)


    override fun onCleared() {
        Timber.e("Main ViewModel is cleared, stop listening")
        super.onCleared()
    }
}
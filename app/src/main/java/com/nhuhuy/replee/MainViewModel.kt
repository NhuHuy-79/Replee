package com.nhuhuy.replee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.AuthenticatedState
import com.nhuhuy.core.domain.repository.PresenceRepository
import com.nhuhuy.replee.core.common.data.data_store.AppDataStore
import com.nhuhuy.replee.core.common.data.data_store.ThemeMode
import com.nhuhuy.replee.core.network.manager.ConnectivityObserver
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(
    connectionObserver: ConnectivityObserver,
    appDataStore: AppDataStore,
    private val presenceRepository: PresenceRepository,
    sessionManager: SessionManager,
) : ViewModel(){
    val network = connectionObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkStatus.Online
        )
    val authenticatedState: StateFlow<AuthenticatedState> = sessionManager.authenticatedState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthenticatedState.Loading
        )

    init {
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


    val theme = appDataStore.observeTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DEFAULT)


    override fun onCleared() {
        Timber.e("Main ViewModel is cleared, stop listening")
        super.onCleared()
    }
}
package com.nhuhuy.replee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode
import com.nhuhuy.replee.worker.ListenConversationsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingDataStore: SettingDataStore,
    private val listenConversationsManager: ListenConversationsManager,
) : ViewModel(){
    init {
        listenToNetworkConversations()
    }

    private fun listenToNetworkConversations() {
        listenConversationsManager.build()
            .launchIn(viewModelScope)
    }
    val logged = authRepository.isUserLogged()
    private val _loggedFlow = MutableStateFlow(logged)
    val loggedFlow = _loggedFlow.asStateFlow()

    val theme = settingDataStore.observeTheme().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DEFAULT)

    override fun onCleared() {
        Timber.e("Main ViewModel is cleared, stop listening")
        super.onCleared()
    }
}
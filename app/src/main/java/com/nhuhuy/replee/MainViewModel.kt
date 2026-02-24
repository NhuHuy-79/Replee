package com.nhuhuy.replee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingDataStore: SettingDataStore,
    private val listenDataManager: ListenDataManager,
) : ViewModel(){
    private var syncJob: Job? = null

    init {
        listenToNetworkConversations()
    }

    private fun listenToNetworkConversations() {
        if (syncJob?.isActive == true) return

        syncJob = viewModelScope.launch {
            listenDataManager.listenableConversationFlow.collect { dataChanges ->
                Timber.d("Data Change: $dataChanges")
                listenDataManager.updateConversationChange(dataChanges)
            }
        }

    }
    val logged = authRepository.isUserLogged()
    private val _loggedFlow = MutableStateFlow(logged)
    val loggedFlow = _loggedFlow.asStateFlow()

    val theme = settingDataStore.observeTheme().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DEFAULT)

    override fun onCleared() {
        syncJob?.cancel()
        syncJob = null
        Timber.e("Main ViewModel is cleared, stop listening")
        super.onCleared()
    }
}
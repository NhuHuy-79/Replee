package com.nhuhuy.replee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.data.data_store.ThemeMode
import com.nhuhuy.replee.core.network.manager.ConnectivityObserver
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import com.nhuhuy.replee.usecase.ObserveThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
    connectionObserver: ConnectivityObserver,
    observeThemeUseCase: ObserveThemeUseCase,
) : ViewModel(){
    val network = connectionObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkStatus.Online
        )

    val theme = observeThemeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DEFAULT)


}
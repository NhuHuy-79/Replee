package com.nhuhuy.replee.navigation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.usecase.CheckAuthenticatedUseCase
import com.nhuhuy.replee.usecase.ObserveAuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    observeAuthenticationUseCase: ObserveAuthenticationUseCase,
    checkAuthenticatedUseCase: CheckAuthenticatedUseCase,
) : ViewModel() {

    val authenticatedState: StateFlow<String?> = observeAuthenticationUseCase()
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), checkAuthenticatedUseCase()
        )
}
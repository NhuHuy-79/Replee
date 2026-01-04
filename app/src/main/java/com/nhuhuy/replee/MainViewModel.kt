package com.nhuhuy.replee

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){
    private val isLogged = authRepository.isUserLogged()
    private val _state: Flow<Boolean> = flow {
        authRepository.provideCurrentUser().onSuccess {
            emit(true)
        }.onFailure {
            emit(false)
        }
    }
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), isLogged)
}
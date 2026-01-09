package com.nhuhuy.replee

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.math.log

@Immutable
data class MainState(
    val isLogged: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.DEFAULT
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingDataStore: SettingDataStore,
) : ViewModel(){
    private val isLogged = authRepository.isUserLogged()
    private val _logged: Flow<Boolean> = flow {
        authRepository.provideCurrentUser().onSuccess {
            emit(true)
        }.onFailure {
            emit(false)
        }
    }
    val logged = _logged.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), isLogged)

    val theme = settingDataStore.observeTheme().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DEFAULT)

    val state = combine(
        logged,
        theme
    ){ logged, theme ->
        MainState(
            isLogged = logged,
            themeMode = theme
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainState())

}
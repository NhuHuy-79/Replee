package com.nhuhuy.replee

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.data.data_store.SeedColor
import com.nhuhuy.replee.core.data.data_store.ThemeMode
import com.nhuhuy.replee.core.network.manager.ConnectivityObserver
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import com.nhuhuy.replee.feature_chat.domain.usecase.option.ObserveChatColorUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.option.ObserveThemeUseCase
import com.nhuhuy.replee.usecase.CheckAuthenticatedUseCase
import com.nhuhuy.replee.usecase.ObserveAuthenticationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

@Immutable
data class MainState(
    val dynamicColor: SeedColor = SeedColor.SAPPHIRE,
    val themeMode: ThemeMode = ThemeMode.DEFAULT,
    val authenticationState: String? = null,
    val showSplashScreen: Boolean = true,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    connectionObserver: ConnectivityObserver,
    observeThemeUseCase: ObserveThemeUseCase,
    observeChatColorUseCase: ObserveChatColorUseCase,
    observeAuthenticationUseCase: ObserveAuthenticationUseCase,
    checkAuthenticatedUseCase: CheckAuthenticatedUseCase,
) : ViewModel() {

    val network = connectionObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkStatus.Online
        )

    val state = combine(
        observeThemeUseCase(),
        observeChatColorUseCase(),
        observeAuthenticationUseCase()
    ) { theme, color, uid ->
        MainState(
            themeMode = theme,
            dynamicColor = color,
            authenticationState = uid,
            showSplashScreen = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = MainState(authenticationState = checkAuthenticatedUseCase())
    )
}
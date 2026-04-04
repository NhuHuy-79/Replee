package com.nhuhuy.replee

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.data.data_store.ThemeMode
import com.nhuhuy.replee.core.design_system.theme.DynamicRepleeTheme
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import com.nhuhuy.replee.feature_chat.utils.toPrimaryColor
import com.nhuhuy.replee.navigation.MainGraph
import dagger.hilt.android.AndroidEntryPoint

val LocalNetworkStatus =
    compositionLocalOf<NetworkStatus> { NetworkStatus.Online }

val LocalMainUiState = compositionLocalOf<MainState> { MainState() }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val state: MainState by viewModel.state.collectAsStateWithLifecycle()
            val network by viewModel.network.collectAsStateWithLifecycle()

            val appTheme: Boolean = when (state.themeMode) {
                ThemeMode.DEFAULT -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            LaunchedEffect(appTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (!appTheme) {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (!appTheme) {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    }
                )
            }
            CompositionLocalProvider(
                LocalNetworkStatus provides network,
                LocalMainUiState provides state
            ) {
                DynamicRepleeTheme(
                    seedColor = state.dynamicColor.toPrimaryColor(),
                    isDark = appTheme
                ) {
                    MainGraph()
                }
            }
        }
    }
}

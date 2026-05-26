package com.nhuhuy.replee

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.common.di.ScopeHolder
import com.nhuhuy.replee.core.design_system.theme.DynamicRepleeTheme
import com.nhuhuy.replee.core.model.settings.ThemeMode
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import com.nhuhuy.replee.feature_chat.utils.toPrimaryColor
import com.nhuhuy.replee.navigation.MainGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val LocalNetworkStatus =
    compositionLocalOf<NetworkStatus> { NetworkStatus.Online }

val LocalMainUiState = compositionLocalOf { MainState() }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var scopeHolder: ScopeHolder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value.showSplashScreen
        }
        setContent {
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
                    MainGraph(
                        userId = state.currentUserId,
                        scopeHolder = scopeHolder
                    )
                }
            }
        }
    }
}

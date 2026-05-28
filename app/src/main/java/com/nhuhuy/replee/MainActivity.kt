package com.nhuhuy.replee

import android.content.Intent
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
import com.nhuhuy.replee.deeplink.DeepLinkDispatcher
import com.nhuhuy.replee.feature_chat.utils.toPrimaryColor
import com.nhuhuy.replee.navigation.AuthDestination
import com.nhuhuy.replee.navigation.HomeDestination
import com.nhuhuy.replee.navigation.MainGraph
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

val LocalNetworkStatus =
    compositionLocalOf<NetworkStatus> { NetworkStatus.Online }

val LocalMainUiState = compositionLocalOf { MainState() }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val deepLinkDispatcher by lazy { DeepLinkDispatcher() }

    @Inject
    lateinit var scopeHolder: ScopeHolder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent = intent)
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
            val startDestination = state.currentUserId?.let { HomeDestination.ConversationList(it) }
                ?: AuthDestination.Login
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
                        scopeHolder = scopeHolder,
                        deepLinkDispatcher = deepLinkDispatcher,
                        startDestination = startDestination,
                        isLogged = state.currentUserId != null
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.dataString
        Timber.tag("DeepLinkDispatcher").d("MainActivity: handleIntent raw URI: $uri")
        if (uri != null) {
            deepLinkDispatcher.submitIntent(uri)
        }
    }
}

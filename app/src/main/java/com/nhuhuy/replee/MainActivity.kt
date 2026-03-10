package com.nhuhuy.replee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.common.data.data_store.ThemeMode
import com.nhuhuy.replee.core.design_system.theme.RepleeTheme
import com.nhuhuy.replee.core.network.manager.NetworkStatus
import com.nhuhuy.replee.navigation.MainGraph
import dagger.hilt.android.AndroidEntryPoint

val LocalNetworkStatus =
    compositionLocalOf<NetworkStatus> { NetworkStatus.Online }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val authState by viewModel.authState.collectAsStateWithLifecycle()
            val theme by viewModel.theme.collectAsStateWithLifecycle()
            val network by viewModel.network.collectAsStateWithLifecycle()
            CompositionLocalProvider(
                LocalNetworkStatus provides network
            ) {
                RepleeTheme(
                    darkTheme = when (theme) {
                        ThemeMode.DEFAULT -> isSystemInDarkTheme()
                        ThemeMode.DARK -> true
                        ThemeMode.LIGHT -> false
                    }
                ) {
                    MainGraph(
                        authState = authState
                    )
                }
            }
        }
    }
}

package com.nhuhuy.replee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.design_system.theme.RepleeTheme
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode
import com.nhuhuy.replee.navigation.MainGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            RepleeTheme(
                darkTheme = when (state.themeMode) {
                    ThemeMode.DEFAULT -> isSystemInDarkTheme()
                    ThemeMode.DARK -> true
                    ThemeMode.LIGHT -> false
                }
            ) {
                MainGraph(isLogged = state.isLogged)
            }
        }
    }
}

package com.nhuhuy.replee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.design_system.theme.RepleeTheme
import com.nhuhuy.replee.feature_auth.data.GoogleIdTokenProvider
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode
import com.nhuhuy.replee.navigation.MainGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    private val googleCredential: GoogleIdTokenProvider by lazy {
        GoogleIdTokenProviderImp(
            context = this,
            logger = logger,
            ioDispatcher = ioDispatcher
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val logged by viewModel.loggedFlow.collectAsStateWithLifecycle()
            val theme by viewModel.theme.collectAsStateWithLifecycle()
            RepleeTheme(
                darkTheme = when (theme) {
                    ThemeMode.DEFAULT -> isSystemInDarkTheme()
                    ThemeMode.DARK -> true
                    ThemeMode.LIGHT -> false
                }
            ) {
                MainGraph(isLogged = logged)
            }
        }
    }
}

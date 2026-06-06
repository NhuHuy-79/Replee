package com.nhuhuy.replee.feature_chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nhuhuy.replee.core.presentation.ObserveEffect
import com.nhuhuy.replee.feature_chat.presentation.option.OptionScreen
import com.nhuhuy.replee.feature_chat.presentation.option.OptionViewModel
import com.nhuhuy.replee.feature_chat.presentation.option.state.OptionEvent

@Composable
fun OptionRoute(
    viewModel: OptionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToConversation: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val color by viewModel.themeColor.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.event) { event ->
        when (event) {
            OptionEvent.NavigateBack -> onNavigateBack()
            OptionEvent.NavigateToConversation -> onNavigateToConversation()
        }
    }
    OptionScreen(
        color = color,
        state = state,
        onAction = viewModel::onAction
    )
}

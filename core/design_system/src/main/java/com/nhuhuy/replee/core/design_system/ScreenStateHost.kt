package com.nhuhuy.replee.core.design_system

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nhuhuy.replee.core.common.base.ScreenState
import com.nhuhuy.replee.core.common.error.Failure

@Composable
fun <T> ScreenStateHost(
    modifier: Modifier = Modifier,
    state: ScreenState<T>,
    success: @Composable (T) -> Unit,
    idle: @Composable () -> Unit = {},
    failure: @Composable (Failure) -> Unit,
    loading: @Composable () -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = state,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        contentKey = { state -> state::class }
    ) { screenState ->
        when (screenState) {
            is ScreenState.Success -> success(screenState.data)
            is ScreenState.Error -> failure(screenState.error)
            ScreenState.Idle -> idle()
            ScreenState.Loading -> loading()
        }
    }
}
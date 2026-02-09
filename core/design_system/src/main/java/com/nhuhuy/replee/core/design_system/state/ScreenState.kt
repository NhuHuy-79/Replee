package com.nhuhuy.replee.core.design_system.state

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.data.model.Failure
import com.nhuhuy.replee.core.common.toRemoteFailure

@Stable
sealed interface ScreenState<out T>{
    data object Idle: ScreenState<Nothing>
    data class Success<out T>(val data: T) : ScreenState<T>
    data object Loading: ScreenState<Nothing>
    data class Error(val error: Failure) : ScreenState<Nothing>
}

fun <T> NetworkResult<T>.toScreenState(): ScreenState<T> {
    return when (this) {
        is NetworkResult.Failure -> ScreenState.Error(throwable.toRemoteFailure())
        is NetworkResult.Success -> ScreenState.Success(data)
    }
}

@Composable
fun <T>ScreenStateHost(
    modifier: Modifier= Modifier,
    state: ScreenState<T>,
    success: @Composable (data: T) -> Unit,
    failure: @Composable () -> Unit,
    loading: @Composable () -> Unit,
    idle: @Composable () -> Unit = {},
){
    Box(
        modifier = modifier,
    ){
        AnimatedContent(
            modifier = Modifier,
            targetState = state,
            contentKey = { it::class },
            transitionSpec = { fadeIn() togetherWith fadeOut()}
        ) { state ->
            when (state) {
                ScreenState.Loading -> loading()
                is ScreenState.Success -> success(state.data)
                is ScreenState.Error -> failure()
                ScreenState.Idle -> idle()
            }
        }
    }
}
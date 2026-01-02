package com.nhuhuy.replee.core.design_system.state

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.nhuhuy.replee.core.common.error_handling.Failure
import com.nhuhuy.replee.core.common.error_handling.Resource

@Stable
interface ScreenState<out T>{
    data class Success<out T>(val data: T) : ScreenState<T>
    data object Loading: ScreenState<Nothing>
    data class Error(val error: Failure) : ScreenState<Nothing>
}


fun <D, F : Failure> Resource<D, F>.toScreenState(): ScreenState<D> {
    return when (this) {
        is Resource.Error<D, F> -> ScreenState.Error(error)
        is Resource.Success<D, F> -> ScreenState.Success(data)
    }
}

@Composable
fun <T>ScreenStateHost(
    modifier: Modifier= Modifier,
    state: ScreenState<T>,
    success: @Composable (T) -> Unit,
    failure: @Composable () -> Unit,
    loading: @Composable () -> Unit,
){
    AnimatedContent(
        modifier = modifier,
        targetState = state,
        contentKey = { it::class },
        transitionSpec = { fadeIn() togetherWith fadeOut()}
    ) { state ->
        when (state) {
            ScreenState.Loading -> loading()
            is ScreenState.Success -> success(state.data)
            is ScreenState.Error -> failure()
        }
    }
}
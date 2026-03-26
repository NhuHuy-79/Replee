package com.nhuhuy.replee.core.common.base

import androidx.compose.runtime.Stable
import com.nhuhuy.replee.core.common.error.RemoteFailure

@Stable
sealed interface ScreenState<out T> {
    data object Idle : ScreenState<Nothing>
    data class Success<out T>(val data: T) : ScreenState<T>
    data object Loading : ScreenState<Nothing>
    data class Error(val error: RemoteFailure) : ScreenState<Nothing>
}
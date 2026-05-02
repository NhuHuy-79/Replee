package com.nhuhuy.replee.core.presentation

import androidx.compose.runtime.Stable

@Stable
sealed interface UiResult<out T> {
    data object Loading : UiResult<Nothing>
    data class Success<out T>(val data: T) : UiResult<T>
    data object Error : UiResult<Nothing>
}


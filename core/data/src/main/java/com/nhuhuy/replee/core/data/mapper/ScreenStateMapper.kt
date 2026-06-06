package com.nhuhuy.replee.core.data.mapper

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.common.base.ScreenState

fun <T> NetworkResult<T>.toScreenState(): ScreenState<T> {
    return when (this) {
        is NetworkResult.Failure -> ScreenState.Error(throwable.toRemoteFailure())
        is NetworkResult.Success -> ScreenState.Success(data)
    }
}

package com.nhuhuy.core.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Failure(val throwable: Throwable) : NetworkResult<Nothing>
}

inline fun <T> NetworkResult<T>.onSuccess(
    block: (T) -> Unit,
): NetworkResult<T> {
    if (this is NetworkResult.Success) {
        block(data)
    }
    return this
}

inline fun <T, R> NetworkResult<T>.map(
    transform: (T) -> R
): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Failure -> NetworkResult.Failure(throwable)
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
    }
}

inline fun <T, R> Flow<NetworkResult<T>>.mapNetworkResult(
    crossinline transform: (T) -> R
): Flow<NetworkResult<R>> {
    return this.map { result ->
        result.map(transform)
    }
}

inline fun <T> NetworkResult<T>.onFailure(
    block: (Throwable) -> Unit,
): NetworkResult<T> {
    if (this is NetworkResult.Failure) {
        block(throwable)
    }
    return this
}

fun <T> NetworkResult<T>.getDataOrNull(): T? {
    return when (this) {
        is NetworkResult.Failure -> null
        is NetworkResult.Success -> data
    }
}


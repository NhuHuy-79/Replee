package com.nhuhuy.replee.core.common.error_handling

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Failure(val throwable: Throwable) : NetworkResult<Nothing>
}

suspend inline fun <T> safeCall(
    crossinline call: suspend () -> T,
    crossinline logger: (Throwable) -> Unit
): NetworkResult<T> {
    return try {
        val data = call()
        NetworkResult.Success(data)
    } catch (e: Exception) {
        logger(e)
        NetworkResult.Failure(e)
    }
}

inline fun <T> NetworkResult<T>.onSuccess(
    block: (T) -> Unit,
): NetworkResult<T> {
    if (this is NetworkResult.Success) {
        block(data)
    }
    return this
}

inline fun <T> NetworkResult<T>.onFailure(
    block: (Throwable) -> Unit,
): NetworkResult<T> {
    if (this is NetworkResult.Failure) {
        block(throwable)
    }
    return this
}


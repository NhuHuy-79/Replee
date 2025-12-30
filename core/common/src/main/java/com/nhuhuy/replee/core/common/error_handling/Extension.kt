package com.nhuhuy.replee.core.common.error_handling

suspend inline fun <D, F : Failure> Resource<D, F>.onSuccessSuspend(
    block: suspend (data: D) -> Unit
): Resource<D, F> {
    if (this is Resource.Success) {
        block(this.data)
    }
    return this
}

inline fun <D, F : Failure> Resource<D, F>.onFailureSuspend(
    block: (error: F) -> Unit
): Resource<D, F> {
    if (this is Resource.Failure) {
        block(this.error)
    }
    return this
}

inline fun <D, F : Failure> Resource<D, F>.onSuccess(
    block: (data: D) -> Unit
): Resource<D, F> {
    if (this is Resource.Success) {
        block(this.data)
    }
    return this
}

suspend inline fun <D, F : Failure> Resource<D, F>.onFailure(
    block: suspend (error: F) -> Unit
): Resource<D, F> {
    if (this is Resource.Failure) {
        block(this.error)
    }
    return this
}

inline fun <D, F : Failure, R> Resource<D, F>.mapSuccess(
    transform: (D) -> R
): Resource<R, F> {
    return when (this) {
        is Resource.Failure<D, F> -> Resource.Failure(error)
        is Resource.Success<D, F> -> Resource.Success(transform(data))
    }
}

suspend inline fun <D, F : Failure, R> Resource<D, F>.mapSuccessSuspend(
    transform: suspend (D) -> R
): Resource<R, F> {
    return when (this) {
        is Resource.Failure<D, F> -> Resource.Failure(error)
        is Resource.Success<D, F> -> Resource.Success(transform(data))
    }
}

suspend inline fun <D, F : Failure> safeCall(
    errorMapper: (e: Exception) -> F,
    block: suspend () -> D,
): Resource<D, F> {
    return try {
        Resource.Success(block())
    } catch (e: Exception) {
        Resource.Failure(errorMapper(e))
    }
}


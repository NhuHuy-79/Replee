package com.nhuhuy.replee.core.common.utils

import com.nhuhuy.core.domain.model.NetworkResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber


suspend fun <T> executeWithTimeout(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    timeout: Long = 5000L,
    body: suspend () -> T
): NetworkResult<T> {
    return withTimeout(timeout) {
        withContext(dispatcher) {
            try {
                val data = body()
                NetworkResult.Success(data)
            } catch (e: CancellationException) {
                Timber.e(e)
                throw e
            } catch (e: Exception) {

                Timber.e(e)
                NetworkResult.Failure(e)
            }
        }
    }
}

suspend fun <T> execute(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    body: suspend () -> T,
): NetworkResult<T> {
    return withContext(dispatcher) {
        try {
            val data = body()
            NetworkResult.Success(data)
        } catch (e: CancellationException) {
            Timber.e(e)
            throw e
        } catch (e: Exception) {
            Timber.e(e)
            NetworkResult.Failure(e)
        }
    }
}

suspend fun <T, R> NetworkResult<T>.flatMap(
    transform: suspend (T) -> NetworkResult<R>
): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Failure -> this
        is NetworkResult.Success -> transform(this.data)
    }
}

suspend fun <T, R> NetworkResult<T>.andThen(
    transform: suspend (T) -> NetworkResult<R>
): NetworkResult<T> {
    return when (this) {
        is NetworkResult.Failure -> this
        is NetworkResult.Success -> {
            transform(this.data)
            this
        }
    }
}




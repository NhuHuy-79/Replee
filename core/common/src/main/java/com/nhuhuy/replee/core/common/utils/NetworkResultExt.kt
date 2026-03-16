package com.nhuhuy.replee.core.common.utils

import com.nhuhuy.core.domain.model.NetworkResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber


suspend inline fun <T> executeWithTimeout(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    timeout: Long = 10_000L,
    crossinline body: suspend () -> T
): NetworkResult<T> {
    return withTimeout(timeout) {
        withContext(dispatcher) {
            try {
                val data = body()
                NetworkResult.Success(data)
            } catch (e: TimeoutCancellationException) {
                NetworkResult.Failure(e)
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

suspend inline fun <T> execute(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline body: suspend () -> T,
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


//Success - flatMap - Failure => Failure
suspend inline fun <T, R> NetworkResult<T>.flatMap(
    transform: suspend (T) -> NetworkResult<R>
): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Failure -> this
        is NetworkResult.Success -> transform(this.data)
    }
}

// Success - andThen - Failure => Success
suspend inline fun <T, R> NetworkResult<T>.andThen(
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




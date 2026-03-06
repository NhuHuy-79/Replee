package com.nhuhuy.replee.core.common.utils

import com.nhuhuy.core.domain.model.NetworkResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber


suspend fun <T> ioExecuteWithTimeout(
    timeout: Long = 5000L,
    body: suspend () -> T
): NetworkResult<T> {
    return withTimeout(timeout) {
        withContext(Dispatchers.IO) {
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

suspend fun <T> ioExecute(
    body: suspend () -> T,
): NetworkResult<T> {
    return withContext(Dispatchers.IO) {
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

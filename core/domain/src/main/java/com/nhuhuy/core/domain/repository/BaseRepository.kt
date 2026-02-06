package com.nhuhuy.core.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.utils.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseRepository(
    private val dispatcher: CoroutineDispatcher,
    private val logger: Logger
) {
    suspend fun <T> safeCallWithTimeout(
        timeOut: Long = 10_000L,
        call: suspend () -> T
    ): NetworkResult<T> {
        return try {
            withTimeout(timeOut) {
                withContext(dispatcher) {
                    NetworkResult.Success(call())
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException && e !is TimeoutCancellationException) {
                throw e
            }
            logger.logException(e)
            NetworkResult.Failure(e)
        }
    }

    suspend fun <T> safeCall(
        call: suspend () -> T
    ): NetworkResult<T> {
        return withContext(dispatcher) {
            try {
                val data = call()
                NetworkResult.Success(data)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.logException(e)
                NetworkResult.Failure(e)
            }
        }
    }
}
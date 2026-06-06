package com.nhuhuy.replee.core.network.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import timber.log.Timber


suspend inline fun <T> FirebaseFirestore.multipleRunBatch(
    items: List<T>,
    noinline block: (T, WriteBatch) -> Unit,
    chunkedSize: Int = 400,
) {
    optimizedWrite(
        items = items,
        singleWrite = { item ->
            this.runBatch { batch ->
                block(item, batch)
            }.await()
        },
        batchWrite = { list ->
            this.runBatch { batch ->
                for (item in list) {
                    block(item, batch)
                }
            }.await()
        },
        batchSize = chunkedSize
    )
}

/**
 * Optimizes write operations by selecting the most efficient method based on the number of items.
 *
 * - If the list is empty, it does nothing.
 * - If there is only one item, it uses [singleWrite].
 * - If the number of items is within the [batchSize], it performs a single [batchWrite].
 * - If the number of items exceeds [batchSize], it splits the items into chunks and performs multiple [batchWrite] operations.
 *
 * @param T The type of items to be written.
 * @param items The list of items to write.
 * @param singleWrite A suspend function to handle a single item write operation.
 * @param batchWrite A suspend function to handle a batch write operation for a list of items.
 * @param batchSize The maximum number of items allowed in a single batch (defaults to 400).
 */
suspend inline fun <T> optimizedWrite(
    items: List<T>,
    singleWrite: suspend (T) -> Unit,
    batchWrite: suspend (List<T>) -> Unit,
    batchSize: Int = 400
) {
    when {
        items.isEmpty() -> return
        items.size == 1 -> {
            singleWrite(items.first())
        }
        items.size <= batchSize -> {
            batchWrite(items)
        }
        else -> {
            items.chunked(batchSize).forEach { chunk ->
                batchWrite(chunk)
            }
        }
    }
}

suspend inline fun <T, R> optimizeRead(
    items: List<T>,
    crossinline action: suspend (List<T>) -> List<R>
): List<R> = coroutineScope {
    if (items.isEmpty()) return@coroutineScope emptyList()

    val chunks = items.distinct().chunked(30)
    val deferredResults = chunks.map { chunk ->
        async {
            try {
                action(chunk)
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.tag("OptimizedRead").e("Error in optimizedRead: ${e.message}")
                emptyList()
            }
        }
    }

    deferredResults.awaitAll().flatten()
}

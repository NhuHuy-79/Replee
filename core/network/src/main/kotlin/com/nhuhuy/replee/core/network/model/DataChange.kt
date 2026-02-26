package com.nhuhuy.replee.core.network.model

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

sealed class DataChange<out T> {
    data class Upsert<out T>(val data: T) : DataChange<T>()
    data class Delete(val id: String) : DataChange<Nothing>()
}

inline fun <T, R> DataChange<T>.mapData(
    transformer: (T) -> R
): DataChange<R> {
    return when (this) {
        is DataChange.Delete -> DataChange.Delete(id)
        is DataChange.Upsert -> DataChange.Upsert(transformer(data))
    }
}

/**
 * Observes changes in a Firestore query as a [Flow] of [DataChange] lists.
 *
 * This function converts a Firestore snapshot listener into a cold stream of data changes.
 * It handles three types of document changes:
 * - ADDED: Mapped to [DataChange.Upsert]
 * - MODIFIED: Mapped to [DataChange.Upsert]
 * - REMOVED: Mapped to [DataChange.Delete]
 *
 * The flow ignores snapshots with pending writes to ensure data consistency with the server.
 * When the flow collector is cancelled, the underlying Firestore snapshot listener is automatically removed.
 *
 * @param T The type to which the Firestore documents should be parsed.
 * @return A [Flow] emitting lists of [DataChange] representing the updates in the query results.
 */
inline fun <reified T> Query.observeDataChange(): Flow<List<DataChange<T>>> =
    callbackFlow {
        val registration = addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot == null) return@addSnapshotListener

            /*  if (snapshot.metadata.hasPendingWrites()) return@addSnapshotListener*/

            val changes = snapshot.documentChanges.mapNotNull { change ->
                when (change.type) {
                    DocumentChange.Type.ADDED,
                    DocumentChange.Type.MODIFIED -> {
                        val dto = runCatching { change.document.toObject<T>() }.getOrNull()
                        dto?.let { DataChange.Upsert(it) }
                    }

                    DocumentChange.Type.REMOVED -> {
                        DataChange.Delete(change.document.id)
                    }
                }
            }

            if (changes.isNotEmpty()) {
                trySend(changes)
            }
        }

        awaitClose { registration.remove() }
    }
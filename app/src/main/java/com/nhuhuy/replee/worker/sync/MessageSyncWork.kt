package com.nhuhuy.replee.worker.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_chat.data.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

object SyncKey {
    const val FAILURE = "Sync Failure"
}

@HiltWorker
class MessageSyncWork @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            //Start worker
            Timber.d("Start Sync Message To Firestore!")

            val resource = syncManager.syncMessage()
            when (resource) {
                is Resource.Success -> {
                    Timber.d("Sync Message To Firestore Success!")

                    Result.success()
                }

                is Resource.Error -> {
                    Timber.d("Failed to Sync Message To Firestore!")

                    if (resource.error is RemoteFailure.Network) {
                        Result.retry()
                    } else {
                        Result.failure(
                            workDataOf(
                                SyncKey.FAILURE to "${resource.error}"
                            )
                        )
                    }
                }
            }
        }
    }
}
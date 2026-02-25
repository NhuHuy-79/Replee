package com.nhuhuy.replee.worker.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.FirebaseNetworkException
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.worker.clean_up.CleanUpDatabaseWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

object SyncKey {
    const val FAILURE = "Sync Failure"
    const val SUCCESS = "Sync Success"
}
private const val CLEAN_UP_WORKER_KEY = "clean_up_worker"

@HiltWorker
class SyncMessageWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            Timber.d("Start Sync Message To Firestore!")

            val result = syncManager.syncMessage()
            when (result) {
                is NetworkResult.Success -> {
                    Timber.d("Sync Message To Firestore Success!")

                    val request = OneTimeWorkRequestBuilder<CleanUpDatabaseWorker>().build()
                    WorkManager.getInstance(context)
                        .enqueueUniqueWork(
                            CLEAN_UP_WORKER_KEY,
                            ExistingWorkPolicy.REPLACE,
                            request
                        )
                    Timber.d("Start Clean Up Worker!")

                    Result.success(
                        workDataOf(
                            SyncKey.SUCCESS to "Sync Success"
                        )
                    )

                }

                is NetworkResult.Failure -> {
                    Timber.d("Failed to Sync Message To Firestore!")

                    if (result.throwable is FirebaseNetworkException) {
                        Result.retry()
                    } else {
                        Result.failure(
                            workDataOf(
                                SyncKey.FAILURE to "${result.throwable}"
                            )
                        )
                    }
                }
            }
        }
    }
}
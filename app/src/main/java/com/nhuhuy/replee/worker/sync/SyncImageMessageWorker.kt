package com.nhuhuy.replee.worker.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.FirebaseNetworkException
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.data.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class SyncImageMessageWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            Timber.d("Start Sync Image To Firestore!")
            val result = syncManager.syncImageMessage()
            when (result) {
                is NetworkResult.Success -> {
                    Timber.d("Sync Image To Firestore Success!")
                    Result.success(
                        workDataOf(
                            SyncKey.SUCCESS to "Sync Success"
                        )
                    )
                }

                is NetworkResult.Failure -> {
                    Timber.d("Failed to Sync Image To Firestore!")
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
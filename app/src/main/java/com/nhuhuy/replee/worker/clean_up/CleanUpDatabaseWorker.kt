package com.nhuhuy.replee.worker.clean_up

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.replee.feature_chat.data.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

@HiltWorker
class CleanUpDatabaseWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val syncManager: SyncManager,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            Timber.d("Start Clean Up Worker!")
            try {
                syncManager.cleanUpDatabase()
                Timber.d("Clean Up Worker Success!")
                Result.success()
            } catch (e: Exception) {
                Timber.e(e, "Clean Up Worker Failed!")
                Result.retry()
            }
        }
    }
}
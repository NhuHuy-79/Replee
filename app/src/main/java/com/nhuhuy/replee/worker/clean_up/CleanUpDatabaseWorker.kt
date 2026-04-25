package com.nhuhuy.replee.worker.clean_up

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.replee.core.data.utils.IoDispatcher
import com.nhuhuy.replee.feature_chat.data.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class CleanUpDatabaseWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val syncManager: SyncManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
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
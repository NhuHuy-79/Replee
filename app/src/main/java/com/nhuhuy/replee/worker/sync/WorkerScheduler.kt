package com.nhuhuy.replee.worker.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface WorkerScheduler {
     fun scheduleMessageSyncWorker()
}

const val REPEAT_TIME: Long = 15
const val SYNC_WORKER_KEY = "sync_worker"

class WorkerSchedulerImp @Inject constructor(
    @ApplicationContext private val context: Context
) : WorkerScheduler{

    private val workManager = WorkManager.getInstance(context)

    override fun scheduleMessageSyncWorker() {
        val request = PeriodicWorkRequestBuilder<MessageSyncWork>(
            repeatInterval = REPEAT_TIME,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(SYNC_WORKER_KEY)
            .build()

        workManager.enqueueUniquePeriodicWork(SYNC_WORKER_KEY, ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
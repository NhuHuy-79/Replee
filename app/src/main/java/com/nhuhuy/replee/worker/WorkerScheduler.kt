package com.nhuhuy.replee.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nhuhuy.replee.worker.sync.ConversationSyncWorker
import com.nhuhuy.replee.worker.sync.SyncMessageWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface WorkerScheduler {
     fun scheduleMessageSyncWorker()
    fun scheduleConversationSyncWorker()
}

const val REPEAT_TIME: Long = 15L
const val MESSAGE_SYNC_WORKER = "message_sync_worker"
const val CONVERSATION_SYNC_WORKER = "conversation_sync_worker"

class WorkerSchedulerImp @Inject constructor(
    @ApplicationContext private val context: Context
) : WorkerScheduler{

    private val workManager = WorkManager.getInstance(context)

    override fun scheduleMessageSyncWorker() {
        val request = PeriodicWorkRequestBuilder<SyncMessageWorker>(
            repeatInterval = REPEAT_TIME,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(MESSAGE_SYNC_WORKER)
            .build()

        workManager.enqueueUniquePeriodicWork(
            MESSAGE_SYNC_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun scheduleConversationSyncWorker() {
        val request = PeriodicWorkRequestBuilder<ConversationSyncWorker>(
            repeatInterval = REPEAT_TIME,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(CONVERSATION_SYNC_WORKER)
            .build()

        workManager.enqueueUniquePeriodicWork(
            MESSAGE_SYNC_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
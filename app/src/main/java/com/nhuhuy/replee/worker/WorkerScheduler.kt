package com.nhuhuy.replee.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nhuhuy.replee.worker.sync.ConversationSyncWorker
import com.nhuhuy.replee.worker.sync.SyncMessageWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface WorkerScheduler {
     fun scheduleMessageSyncWorker()
    fun scheduleConversationSyncWorker()
}

const val MESSAGE_SYNC_WORKER = "message_sync_worker"
const val CONVERSATION_SYNC_WORKER = "conversation_sync_worker"

class WorkerSchedulerImp @Inject constructor(
    @ApplicationContext private val context: Context
) : WorkerScheduler{

    private val workManager = WorkManager.getInstance(context)

    override fun scheduleMessageSyncWorker() {
        val request = OneTimeWorkRequestBuilder<SyncMessageWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(MESSAGE_SYNC_WORKER)
            .build()

        workManager.enqueueUniqueWork(
            MESSAGE_SYNC_WORKER,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    override fun scheduleConversationSyncWorker() {
        val request = OneTimeWorkRequestBuilder<ConversationSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(CONVERSATION_SYNC_WORKER)
            .build()

        workManager.enqueueUniqueWork(
            CONVERSATION_SYNC_WORKER,
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}
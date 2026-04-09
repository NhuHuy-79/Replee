package com.nhuhuy.replee.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.nhuhuy.replee.feature_chat.data.worker.SaveNewMessageWorker
import com.nhuhuy.replee.feature_chat.data.worker.SyncMessageActionWorker
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.worker.sync.SyncConversationWorker
import com.nhuhuy.replee.worker.sync.SyncFileWorker
import com.nhuhuy.replee.worker.sync.SyncMessageWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

const val MESSAGE_SYNC_WORKER = "message_sync_worker"
const val FILE_SYNC_WORKER = "file_sync_worker"
const val CONVERSATION_SYNC_WORKER = "conversation_sync_worker"
const val SAVE_NEW_MESSAGE_WORKER = "save_new_message_worker"
const val LAST_TIME_KEY = "last time key"
const val MESSAGE_ACTION_WORKER = "message_action_worker"
const val MESSAGE_ID = "message_id"
const val CONVERSATION_ID = "conversationId"
class WorkerSchedulerImp @Inject constructor(
    @ApplicationContext private val context: Context
) : WorkerScheduler {
    private val workManager by lazy { WorkManager.getInstance(context) }

    override fun scheduleFileSyncWorker() {
        val request = OneTimeWorkRequestBuilder<SyncFileWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(FILE_SYNC_WORKER)
            .build()

        workManager.enqueueUniqueWork(
            FILE_SYNC_WORKER,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

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
        val request = OneTimeWorkRequestBuilder<SyncConversationWorker>()
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

    override fun scheduleMessageActionWorker() {
        val request = OneTimeWorkRequestBuilder<SyncMessageActionWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(MESSAGE_ACTION_WORKER)
            .build()

        workManager.enqueueUniqueWork(
            MESSAGE_ACTION_WORKER,
            ExistingWorkPolicy.APPEND,
            request
        )
    }

    override fun scheduleSaveMessageWorker(conversationId: String) {
        val request = OneTimeWorkRequestBuilder<SaveNewMessageWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                inputData = workDataOf(
                    CONVERSATION_ID to conversationId,
                )
            )
            .addTag(SAVE_NEW_MESSAGE_WORKER)
            .build()
        workManager.enqueueUniqueWork(
            SAVE_NEW_MESSAGE_WORKER,
            ExistingWorkPolicy.APPEND,
            request
        )
    }
}
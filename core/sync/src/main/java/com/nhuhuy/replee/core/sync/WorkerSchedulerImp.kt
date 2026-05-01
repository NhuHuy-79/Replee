package com.nhuhuy.replee.core.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.nhuhuy.replee.core.domain.worker.MESSAGE_ID_INPUT
import com.nhuhuy.replee.core.domain.worker.URI_PATH_INPUT
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.sync.worker.SaveNewMessageWorker
import com.nhuhuy.replee.core.sync.worker.SendFileWorker
import com.nhuhuy.replee.core.sync.worker.SyncChatActionWorker
import com.nhuhuy.replee.core.sync.worker.SyncConversationWorker
import com.nhuhuy.replee.core.sync.worker.SyncFileWorker
import com.nhuhuy.replee.core.sync.worker.SyncMessageWorker
import com.nhuhuy.replee.core.sync.worker.UploadAvatarWorker
import com.nhuhuy.replee.core.sync.worker.KEY_UID
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val MESSAGE_SYNC_WORKER = "message_sync_worker"
const val FILE_SYNC_WORKER = "file_sync_worker"
const val CONVERSATION_SYNC_WORKER = "conversation_sync_worker"
const val SAVE_NEW_MESSAGE_WORKER = "save_new_message_worker"
const val LAST_TIME_KEY = "last time key"
const val MESSAGE_ACTION_WORKER = "message_action_worker"
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
        val request = OneTimeWorkRequestBuilder<SyncChatActionWorker>()
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

    override fun scheduleUploadFile(messageId: String, uriPath: String) {
        val input = workDataOf(
            MESSAGE_ID_INPUT to messageId,
            URI_PATH_INPUT to uriPath
        )

        val uploadRequest = OneTimeWorkRequestBuilder<SendFileWorker>()
            .setInputData(inputData = input)
            .setConstraints(
                constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = WorkRequest.MIN_BACKOFF_MILLIS,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            "upload_$messageId",
            ExistingWorkPolicy.KEEP,
            uploadRequest
        )
    }

    override fun scheduleUploadAvatar(uid: String) {
        val uploadRequest = OneTimeWorkRequestBuilder<UploadAvatarWorker>()
            .setInputData(
                inputData = workDataOf(
                    KEY_UID to uid,
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "upload_avatar_$uid",
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        )
    }

    override fun observeUploadAvatar(uid: String): Flow<WorkInfo?> {
        return workManager.getWorkInfosForUniqueWorkFlow("upload_avatar_$uid")
            .map { it.firstOrNull() }
    }
}

package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

interface MessageWorkerScheduler {
    suspend fun scheduleMessageSyncWorker(messageId: String, conversationId: String)
}

const val DELETE_MESSAGE__WORKER = "delete_message_worker"
const val MESSAGE_ID = "message_id"
const val CONVERSATION_ID = "conversationId"

class MessageWorkerSchedulerImp @Inject constructor(
    @ApplicationContext private val context: Context,
) : MessageWorkerScheduler {
    private val workManager by lazy {
        WorkManager.getInstance(context)
    }

    override suspend fun scheduleMessageSyncWorker(messageId: String, conversationId: String) {
        val request = OneTimeWorkRequestBuilder<DeleteMessageWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                inputData = workDataOf(
                    MESSAGE_ID to messageId,
                    CONVERSATION_ID to conversationId
                )
            )
            .addTag(DELETE_MESSAGE__WORKER)
            .build()

        workManager.enqueueUniqueWork(
            messageId,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

}
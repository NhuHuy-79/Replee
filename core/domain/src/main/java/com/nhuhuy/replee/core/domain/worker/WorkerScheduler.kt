package com.nhuhuy.replee.core.domain.worker

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

const val MESSAGE_ID_INPUT = "message_id_input"
const val URI_PATH_INPUT = "uri_path_input"

interface WorkerScheduler {
    fun scheduleConversationSyncWorker()
    fun scheduleFileSyncWorker()
    fun scheduleMessageSyncWorker()
    fun scheduleMessageActionWorker()
    fun scheduleSaveMessageWorker(conversationId: String)
    fun scheduleUploadFile(messageId: String, uriPath: String)
    fun scheduleUploadAvatar(uid: String)
    fun observeUploadAvatar(uid: String): Flow<WorkInfo?>
}

package com.nhuhuy.replee.feature_chat.data.worker

import com.nhuhuy.replee.feature_chat.domain.model.message.Message

interface WorkerScheduler {
    fun scheduleConversationSyncWorker()
    fun scheduleFileSyncWorker()
    fun scheduleMessageSyncWorker()
    fun scheduleDeleteMessage(message: Message)
    fun scheduleSaveNewMessage(conversationId: String)
}

package com.nhuhuy.replee.worker.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.utils.IoDispatcher
import com.nhuhuy.replee.feature_chat.data.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class SyncConversationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val parameters: WorkerParameters,
    private val sessionManager: SessionManager,
    private val syncManager: SyncManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            Timber.d("Start Sync Conversation To Firestore!")
            if (runAttemptCount > 3) {
                return@withContext Result.failure()
            }

            val uid = sessionManager.getUserIdOrNull()

            if (uid == null) {
                Timber.e("User not logged in!")
                return@withContext Result.retry()
            }
            val result = syncManager.syncConversation(uid)
            when (result) {
                is NetworkResult.Failure -> Result.retry()
                is NetworkResult.Success -> Result.success()
            }
        }
    }
}
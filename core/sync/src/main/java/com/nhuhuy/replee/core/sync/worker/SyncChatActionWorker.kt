package com.nhuhuy.replee.core.sync.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.sync.usecase.DeleteConversationSyncUseCase
import com.nhuhuy.replee.core.sync.usecase.DeleteMessagesSyncUseCase
import com.nhuhuy.replee.core.sync.usecase.PinMessageSyncUseCase
import com.nhuhuy.replee.core.sync.usecase.UnPinMessageSyncUseCase
import com.nhuhuy.replee.core.sync.usecase.UpdateReactionSyncUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@HiltWorker
class SyncChatActionWorker @AssistedInject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val deleteMessagesSyncUseCase: DeleteMessagesSyncUseCase,
    private val deleteConversationSyncUseCase: DeleteConversationSyncUseCase,
    private val pinMessageSyncUseCase: PinMessageSyncUseCase,
    private val unPinMessageSyncUseCase: UnPinMessageSyncUseCase,
    private val updateReactionSyncUseCase: UpdateReactionSyncUseCase,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {
            if (runAttemptCount >= 5) return@withContext Result.failure()

            val deleteDeferred = async { deleteMessagesSyncUseCase() }
            val deleteConversationDeferred = async { deleteConversationSyncUseCase() }
            val pinDeferred = async { pinMessageSyncUseCase() }
            val unpinDeferred = async { unPinMessageSyncUseCase() }
            val reactionDeferred = async { updateReactionSyncUseCase() }

            val results = listOf(
                deleteDeferred.await(),
                deleteConversationDeferred.await(),
                pinDeferred.await(),
                unpinDeferred.await(),
                reactionDeferred.await()
            )

            val isAnyFailed = results.any { result -> result is NetworkResult.Failure }

            if (isAnyFailed) {
                Result.retry()
            } else {
                Result.success()
            }
        }
    }
}

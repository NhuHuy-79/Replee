package com.nhuhuy.replee.feature_profile.domain.usecase

import androidx.work.WorkInfo
import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUploadAvatarUseCase @Inject constructor(
    private val workerScheduler: WorkerScheduler
) {
    operator fun invoke(uid: String): Flow<WorkInfo?> {
        return workerScheduler.observeUploadAvatar(uid)
    }
}

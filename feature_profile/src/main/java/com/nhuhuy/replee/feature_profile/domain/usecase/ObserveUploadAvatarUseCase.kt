package com.nhuhuy.replee.feature_profile.domain.usecase

import androidx.work.WorkInfo
import com.nhuhuy.replee.feature_profile.data.worker.ProfileScheduler
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveUploadAvatarUseCase @Inject constructor(
    private val profileScheduler: ProfileScheduler
) {
    operator fun invoke(uuid: UUID): Flow<WorkInfo?> {
        return profileScheduler.observeUploadAvatarWorker(uuid)
    }
}
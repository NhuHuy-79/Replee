package com.nhuhuy.replee.feature_profile.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


const val KEY_UID = "worker_params_uid"
const val KEY_URI = "worker_params_uri"

interface ProfileScheduler {
    suspend fun schedulerUploadAvatar(
        uid: String,
    )
}

class ProfileSchedulerImp @Inject constructor(
    @ApplicationContext private val context: Context
) : ProfileScheduler {
    private val workManager: WorkManager by lazy { WorkManager.getInstance(context) }

    override suspend fun schedulerUploadAvatar(uid: String) {
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

}

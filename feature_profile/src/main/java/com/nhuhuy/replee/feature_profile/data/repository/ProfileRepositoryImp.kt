package com.nhuhuy.replee.feature_profile.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.utils.ioExecute
import com.nhuhuy.replee.core.common.utils.ioExecuteWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.CloudinaryFileUploader
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import timber.log.Timber
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val cloudifyFileUploadService: CloudinaryFileUploader,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val appPreferences: AppPreferences
) : ProfileRepository {
    override suspend fun updateUserImage(uriPath: String): NetworkResult<String> {
        return ioExecute {
            val ownerId = firebaseAuthEmailService.getCurrentUser()?.uid ?: return@ioExecute ""
            val url = cloudifyFileUploadService.uploadImageWithUriPath(uriPath)
            Timber.d(url)
            accountLocalDataSource.updateImageUrl(
                uid = ownerId,
                imgUrl = url
            )
            accountNetworkDataSource.updateImageUrl(
                uid = ownerId,
                imgUrl = url
            )
            url
        }
    }

    override suspend fun logOut() {
        val uid = try {
            firebaseAuthEmailService.getCurrentUser()?.uid
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
        firebaseAuthEmailService.logOut()
        appPreferences.saveLoggedStatus(false)

        uid?.let { uid ->
            accountLocalDataSource.updateLogoutStatus(uid)
        }
    }

    override suspend fun updatePassword(
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit> = ioExecuteWithTimeout {
        firebaseAuthEmailService.updateNewPassword(oldPassword, newPassword)
    }

}
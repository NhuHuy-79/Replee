package com.nhuhuy.replee.feature_profile.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.common.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import timber.log.Timber
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val uploadFileService: UploadFileService,
    private val firebaseAuthEmailService: FirebaseAuthEmailService
) : ProfileRepository {
    override suspend fun updateUserImage(uriPath: String): NetworkResult<String> {
        return execute {
            val ownerId = firebaseAuthEmailService.getCurrentUser()?.uid ?: return@execute ""
            val url = uploadFileService.uploadImageWithUriPath(uriPath)
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

        uid?.let { uid ->
            accountLocalDataSource.updateLogoutStatus(uid)
        }
    }

    override suspend fun updatePassword(
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit> = executeWithTimeout {
        firebaseAuthEmailService.updateNewPassword(oldPassword, newPassword)
    }

}
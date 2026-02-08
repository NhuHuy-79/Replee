package com.nhuhuy.replee.feature_profile.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.CloudifyFileUploadService
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val cloudifyFileUploadService: CloudifyFileUploadService,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val appPreferences: AppPreferences
) : ProfileRepository,
    NetworkResultCaller(ioDispatcher, logger) {
    override suspend fun updateUserImage(byteArray: ByteArray): NetworkResult<String> {
        return safeCall {
            val ownerId = firebaseAuthEmailService.getCurrentUser().uid
            val url = cloudifyFileUploadService.uploadImage(byteArray)
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

    override suspend fun updateNewPassword(
        old: String,
        new: String
    ): Resource<Unit, RemoteFailure> {
        return withContext(ioDispatcher) {
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                firebaseAuthEmailService.updateNewPassword(old, new)
            }
        }
    }

    override suspend fun logOut() {
        val uid = try {
            firebaseAuthEmailService.getCurrentUser().uid
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
    ): NetworkResult<Unit> = safeCallWithTimeout {
        firebaseAuthEmailService.updateNewPassword(oldPassword, newPassword)
    }

}
package com.nhuhuy.replee.feature_profile.data.repository

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.data.data_store.NotificationMode
import com.nhuhuy.replee.core.common.data.data_store.ThemeMode
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.network.quailify.Cloudinary
import com.nhuhuy.replee.feature_profile.data.source.ProfileLocalDataSource
import com.nhuhuy.replee.feature_profile.data.source.ProfileNetworkDataSource
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val profileNetworkDataSource: ProfileNetworkDataSource,
    private val profileLocalDataSource: ProfileLocalDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    @Cloudinary private val uploadFileService: UploadFileService,
    private val sessionManager: SessionManager,
    private val firebaseAuthEmailService: FirebaseAuthEmailService
) : ProfileRepository {
    override suspend fun updateNotification(mode: NotificationMode) {
        profileLocalDataSource.updateNotification(mode)
    }

    override suspend fun updateThemeMode(mode: ThemeMode) {
        profileLocalDataSource.updateTheme(mode)
    }

    override fun observeNotification(): Flow<NotificationMode> {
        return profileLocalDataSource.observeNotification()
    }

    override fun observeTheme(): Flow<ThemeMode> {
        return profileLocalDataSource.observerTheme()
    }

    override suspend fun updateUserImage(uriPath: String): NetworkResult<String> {
        return execute {
            val ownerId = sessionManager.requireUserId()
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

    override suspend fun logOut(): String {
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

        return uid.orEmpty()
    }

    override suspend fun updatePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit> = execute {
        profileNetworkDataSource.updatePassword(email, oldPassword, newPassword)
    }

}
package com.nhuhuy.replee.feature_profile.data.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.data_store.NotificationMode
import com.nhuhuy.replee.core.data.data_store.ThemeMode
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.feature_profile.data.source.ProfileLocalDataSource
import com.nhuhuy.replee.feature_profile.data.source.ProfileNetworkDataSource
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val profileNetworkDataSource: ProfileNetworkDataSource,
    private val profileLocalDataSource: ProfileLocalDataSource,
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

    override suspend fun updatePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit> = execute {
        profileNetworkDataSource.updatePassword(email, oldPassword, newPassword)
    }

}
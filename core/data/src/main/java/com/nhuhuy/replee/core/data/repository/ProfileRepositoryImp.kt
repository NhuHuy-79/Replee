package com.nhuhuy.replee.core.data.repository

import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.database.data_source.ProfileLocalDataSource
import com.nhuhuy.replee.core.domain.repository.ProfileRepository
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.settings.NotificationMode
import com.nhuhuy.replee.core.model.settings.ThemeMode
import com.nhuhuy.replee.core.network.data_source.account.ProfileNetworkDataSource
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

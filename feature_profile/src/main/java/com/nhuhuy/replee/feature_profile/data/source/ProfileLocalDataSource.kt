package com.nhuhuy.replee.feature_profile.data.source

import com.nhuhuy.replee.core.data.data_store.AppDataStore
import com.nhuhuy.replee.core.data.data_store.NotificationMode
import com.nhuhuy.replee.core.data.data_store.ThemeMode
import com.nhuhuy.replee.core.data.utils.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ProfileLocalDataSource {
    suspend fun updateLocalUserImage()
    suspend fun updateNotification(mode: NotificationMode)
    fun observeNotification(): Flow<NotificationMode>
    suspend fun updateTheme(mode: ThemeMode)
    fun observerTheme(): Flow<ThemeMode>
}

class ProfileLocalDataSourceImp @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val appDataStore: AppDataStore,
) : ProfileLocalDataSource {
    override suspend fun updateLocalUserImage() {
        //Update local url for user entity
    }

    override suspend fun updateNotification(mode: NotificationMode) {
        withContext(ioDispatcher) {
            appDataStore.saveNotificationMode(mode)
        }
    }

    override fun observeNotification(): Flow<NotificationMode> {
        return appDataStore.observeNotification()
            .flowOn(ioDispatcher)
    }

    override suspend fun updateTheme(mode: ThemeMode) {
        withContext(ioDispatcher) {
            appDataStore.saveThemeMode(mode)
        }
    }

    override fun observerTheme(): Flow<ThemeMode> {
        return appDataStore.observeTheme()
            .flowOn(ioDispatcher)
    }
}

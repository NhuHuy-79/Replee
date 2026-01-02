package com.nhuhuy.replee.feature_profile.data.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SettingDataStore {
    suspend fun updateNotification(mode: NotificationMode)
    suspend fun updateTheme(mode: ThemeMode)
    fun observeNotification() : Flow<NotificationMode>
    fun observeTheme() : Flow<ThemeMode>
}


class SettingDataStoreImp @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingDataStore{

    companion object {
        val NOTIFICATION_KEY = stringPreferencesKey("notification_key")
        val THEME_KEY = stringPreferencesKey("theme_key")
    }

    override suspend fun updateNotification(mode: NotificationMode) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_KEY] = mode.name
        }
    }

    override suspend fun updateTheme(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }

    override fun observeNotification(): Flow<NotificationMode> {
        return dataStore.data.map { pref ->
            val string = pref[NOTIFICATION_KEY] ?: NotificationMode.NONE.name
            NotificationMode.valueOf(string)
        }
    }

    override fun observeTheme(): Flow<ThemeMode> {
        return dataStore.data.map { pref ->
            val string = pref[THEME_KEY] ?: ThemeMode.DEFAULT.name
            ThemeMode.valueOf(string)
        }
    }

}
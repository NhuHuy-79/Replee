package com.nhuhuy.replee.feature_profile.data.data_store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SettingDataStore {
    suspend fun updateNotification(mode: NotificationMode)
    suspend fun updateTheme(mode: ThemeMode)
    fun observeNotification() : Flow<NotificationMode>
    fun observeTheme() : Flow<ThemeMode>
}

private val Context.dataStore by preferencesDataStore("setting_pref")

class SettingDataStoreImp @Inject constructor(
    private val context: Context
) : SettingDataStore{
    companion object {
        val NOTIFICATION_KEY = stringPreferencesKey("notification_key")
        val THEME_KEY = stringPreferencesKey("theme_key")
    }

    override suspend fun updateNotification(mode: NotificationMode) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_KEY] = mode.name
        }
    }

    override suspend fun updateTheme(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }

    override fun observeNotification(): Flow<NotificationMode> {
        return context.dataStore.data.map { pref ->
            val string = pref[NOTIFICATION_KEY] ?: NotificationMode.NONE.name
            NotificationMode.valueOf(string)
        }
    }

    override fun observeTheme(): Flow<ThemeMode> {
        return context.dataStore.data.map { pref ->
            val string = pref[THEME_KEY] ?: ThemeMode.DEFAULT.name
            ThemeMode.valueOf(string)
        }
    }

}
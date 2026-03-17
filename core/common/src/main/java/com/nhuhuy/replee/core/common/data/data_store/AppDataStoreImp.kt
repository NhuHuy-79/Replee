package com.nhuhuy.replee.core.common.data.data_store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("setting_pref")

class AppDataStoreImp @Inject constructor(
    private val context: Context
) : AppDataStore {
    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val NOTIFICATION_KEY = stringPreferencesKey("notification_key")
        val THEME_KEY = stringPreferencesKey("theme_key")
    }

    override suspend fun saveNotificationMode(mode: NotificationMode) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_KEY] = mode.name
        }
    }

    override suspend fun saveThemeMode(mode: ThemeMode) {
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

    override suspend fun getAuthenticationToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN]
        }.first()
    }

    override suspend fun saveAuthenticationToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

}
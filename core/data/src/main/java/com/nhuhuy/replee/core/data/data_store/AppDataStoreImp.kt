package com.nhuhuy.replee.core.data.data_store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject


private val Context.dataStore by preferencesDataStore("setting_pref")

class AppDataStoreImp @Inject constructor(
    private val context: Context
) : AppDataStore {
    companion object {
        val AUTH_TOKEN = stringPreferencesKey("authentication_token")
        val NOTIFICATION_KEY = stringPreferencesKey("notification_key")
        val THEME_KEY = stringPreferencesKey("theme_key")
        val CHAT_COLOR_KEY = stringPreferencesKey("chat_color_key")
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

    override fun getAuthenticationToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            val token = preferences[AUTH_TOKEN] ?: ""
            Timber.d("DATASTORE_INTERNAL: Đang phát ra Token: '$token'")
            token
        }
    }

    override suspend fun saveAuthenticationToken(token: String) {
        context.dataStore.edit { preferences ->
            Timber.d("SAVE_CHECK: Đã lưu Token mới: $token")
            preferences[AUTH_TOKEN] = token
        }
    }

    override fun observeChatColor(): Flow<SeedColor> {
        return context.dataStore.data.map { preferences ->
            val string = preferences[CHAT_COLOR_KEY] ?: SeedColor.SAPPHIRE.name
            SeedColor.valueOf(string)
        }
    }

    override suspend fun saveChatColor(color: SeedColor) {
        context.dataStore.edit { preferences ->
            preferences[CHAT_COLOR_KEY] = color.name
        }
    }

}
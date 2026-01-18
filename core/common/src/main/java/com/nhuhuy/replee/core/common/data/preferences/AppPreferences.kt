package com.nhuhuy.replee.core.common.data.preferences

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val pref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val loggedStatus = "logged_key"

    fun getLoggedStatus(): Boolean {
        return pref.getBoolean(loggedStatus, false)
    }

    fun saveLoggedStatus(status: Boolean) {
        pref.edit {
            putBoolean(loggedStatus, status)
        }
    }
}
package com.nhuhuy.replee.feature_profile.presentation.profile.state

import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.feature_profile.data.data_store.NotificationMode
import com.nhuhuy.replee.feature_profile.data.data_store.ThemeMode

data class ProfileState(
    val account: Account = Account(),
    val notification: NotificationMode = NotificationMode.NONE,
    val darkMode: ThemeMode = ThemeMode.DEFAULT,
) : UiState

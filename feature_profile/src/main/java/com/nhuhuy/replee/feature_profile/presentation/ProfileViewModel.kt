package com.nhuhuy.replee.feature_profile.presentation

import androidx.datastore.dataStore
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileState
import com.skydoves.flow.operators.onetime.OnetimeWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val dataStore: SettingDataStore,
) : BaseViewModel<ProfileAction, ProfileEvent, ProfileState>() {

    private val account = flow {
        profileRepository.getUserInformation().onSuccess { account ->
            emit(account)
        }.onFailure {
            emit(Account())
        }
    }

    override val state: StateFlow<ProfileState> = combine(
        account,
        dataStore.observeTheme(),
        dataStore.observeNotification()
    ) { account, theme, notification ->
        ProfileState(
            account = account,
            notification = notification,
            darkMode = theme,
        )
    }.stateIn(viewModelScope, SharingStarted.OnetimeWhileSubscribed(5000), ProfileState())

    override fun onAction(action: ProfileAction) {
        viewModelScope.launch {
            when (action) {
                ProfileAction.OnAboutClick -> {
                    onEvent(ProfileEvent.GoToAbout)
                }

                ProfileAction.OnDarkModeClick.Dialog -> {
                    onEvent(ProfileEvent.ShowThemeDialog)
                }
                is ProfileAction.OnDarkModeClick.Select -> {
                    dataStore.updateTheme(action.option)
                    onEvent(ProfileEvent.None)
                }
                ProfileAction.OnNotificationClick.Dialog -> {
                    onEvent(ProfileEvent.ShowNotificationDialog)
                    onEvent(ProfileEvent.None)
                }
                is ProfileAction.OnNotificationClick.Select -> {
                    dataStore.updateNotification(action.option)
                }
            }
        }
    }
}
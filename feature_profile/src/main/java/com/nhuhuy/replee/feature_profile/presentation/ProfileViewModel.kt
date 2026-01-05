package com.nhuhuy.replee.feature_profile.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.Validator
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.repository.AccountRepository
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.core.design_system.component.DynamicInput
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import com.nhuhuy.replee.feature_profile.presentation.profile.state.Overlay
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val validator: Validator,
    private val profileRepository: ProfileRepository,
    private val accountRepository: AccountRepository,
    private val dataStore: SettingDataStore,
) : BaseViewModel<ProfileAction, ProfileEvent, ProfileState>() {

    private val _inputState = MutableStateFlow(InputState())
    private val inputState : StateFlow<InputState> = _inputState.asStateFlow()

    private val _overlayState = MutableStateFlow(Overlay.NONE)
    private val dialogState = _overlayState.asStateFlow()

    private val account = flow {
        accountRepository.getCurrentAccount().onSuccess { account ->
            emit(account)
        }.onFailure {
            emit(Account())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Account())


    override val state: StateFlow<ProfileState> = combine(
        inputState,
        account,
        dataStore.observeTheme(),
        dataStore.observeNotification(),
        dialogState,
    ) { input, account, theme, notification, overlay ->
        ProfileState(
            account = account,
            notification = notification,
            darkMode = theme,
            oldPassword = input.oldPassword,
            newPassword = input.newPassword,
            overlay = overlay,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileState())

    override fun onAction(action: ProfileAction) {
        viewModelScope.launch {
            when (action) {
                ProfileAction.OnAboutClick -> {
                    onEvent(ProfileEvent.GoToAbout)
                }

                ProfileAction.OnDarkModeClick.Dialog -> {
                    _overlayState.update { Overlay.THEME }
                }
                is ProfileAction.OnDarkModeClick.Select -> {
                    dataStore.updateTheme(action.option)
                    _overlayState.update { Overlay.NONE }
                }
                ProfileAction.OnNotificationClick.Dialog -> {
                    _overlayState.update { Overlay.NOTIFICATION }
                }
                is ProfileAction.OnNotificationClick.Select -> {
                    dataStore.updateNotification(action.option)
                    _overlayState.update { Overlay.NONE }
                }

                is ProfileAction.OnOldPasswordChange -> {
                    _inputState.reduce {
                        copy(
                            oldPassword = DynamicInput(
                                text = action.password,
                                validateResult = validator.validatePassword(action.password)
                            )
                        )
                    }
                }
                is ProfileAction.OnNewPasswordChange -> {
                    val old = inputState.value.oldPassword.text
                    _inputState.reduce {
                        copy(
                            newPassword = DynamicInput(
                                action.password,
                                validateResult = validator.validateNewPassword(
                                    old = old,
                                    new = action.password
                                )
                            )
                        )
                    }
                }

                ProfileAction.OnDismiss -> {
                    _overlayState.update { Overlay.NONE }
                }
                ProfileAction.OnLogOut -> {
                    profileRepository.logOut()
                    onEvent(ProfileEvent.GoToSignIn)
                }

                ProfileAction.OnUpdatePassword.BottomSheet -> {
                    _overlayState.update { Overlay.UPDATE_PASSWORD }
                }
                ProfileAction.OnUpdatePassword.Confirm -> {
                    val old = inputState.value.oldPassword.text
                    val new = inputState.value.newPassword.text
                    profileRepository.updatePassword(old = old, new = new)
                        .onSuccess {
                            onEvent(ProfileEvent.UpdatePassword.Success)
                            _overlayState.update { Overlay.NONE }
                        }
                        .onFailure { error ->
                            onEvent(ProfileEvent.UpdatePassword.Failure(error))
                        }
                }
            }
        }
    }

    @Immutable
    data class InputState(
        val newPassword: DynamicInput = DynamicInput(),
        val oldPassword: DynamicInput = DynamicInput(),
    )
}
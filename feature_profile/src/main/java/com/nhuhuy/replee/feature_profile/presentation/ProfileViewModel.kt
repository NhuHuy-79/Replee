package com.nhuhuy.replee.feature_profile.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.data.UriConverter
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.design_system.component.ValidatableInput
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.core.design_system.state.toScreenState
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.domain.usecase.LogOutUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.UpdatePasswordUseCase
import com.nhuhuy.replee.feature_profile.domain.usecase.UploadAvatarUseCase
import com.nhuhuy.replee.feature_profile.presentation.profile.state.Overlay
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileAction
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileActionResult
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent
import com.nhuhuy.replee.feature_profile.presentation.profile.state.ProfileEvent.UpdatePassword.Failure
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
import timber.log.Timber

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val uriConverter: UriConverter,
    private val dataStore: SettingDataStore,
) : BaseViewModel<ProfileAction, ProfileEvent, ProfileState>() {
    private val _profileActionResult = MutableStateFlow(ProfileActionResult())
    val profileActionResult = _profileActionResult.asStateFlow()
    private val _inputState = MutableStateFlow(InputState())
    private val inputState : StateFlow<InputState> = _inputState.asStateFlow()

    private val _overlayState = MutableStateFlow(Overlay.NONE)
    private val dialogState = _overlayState.asStateFlow()

    private val account = flow {
        emit(getCurrentAccountUseCase())
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
                    dataStore.saveThemeMode(action.option)
                    _overlayState.update { Overlay.NONE }
                }
                ProfileAction.OnNotificationClick.Dialog -> {
                    _overlayState.update { Overlay.NOTIFICATION }
                }
                is ProfileAction.OnNotificationClick.Select -> {
                    dataStore.saveNotificationMode(action.option)
                    _overlayState.update { Overlay.NONE }
                }

                is ProfileAction.OnOldPasswordChange -> {
                    _inputState.reduce {
                        copy(
                            oldPassword = ValidatableInput(
                                text = action.password,
                                validateResult = inputValidator.validatePassword(action.password)
                            )
                        )
                    }
                }
                is ProfileAction.OnNewPasswordChange -> {
                    _inputState.reduce {
                        val old = oldPassword.text
                        copy(
                            newPassword = ValidatableInput(
                                text = action.password,
                                validateResult = inputValidator.validateNewPassword(
                                    old = old,
                                    new = action.password
                                )
                            )
                        )
                    }
                }

                ProfileAction.OnNavigateBack -> {
                    onEvent(ProfileEvent.NavigateBack)
                }

                ProfileAction.OnDismiss -> {
                    _profileActionResult.reduce { copy(updatePassword = ScreenState.Idle) }
                    _overlayState.update { Overlay.NONE }
                }
                ProfileAction.OnLogOut -> {
                    logOutUseCase()
                    onEvent(ProfileEvent.GoToSignIn)
                }

                ProfileAction.OnUpdatePassword.BottomSheet -> {
                    _overlayState.update { Overlay.UPDATE_PASSWORD }
                }
                ProfileAction.OnUpdatePassword.Confirm -> {
                    val old = inputState.value.oldPassword.text
                    val new = inputState.value.newPassword.text
                    _profileActionResult.reduce { copy(updatePassword = ScreenState.Loading) }
                    val screenState = updatePasswordUseCase(oldPassword = old, newPassword = new)
                        .onSuccess {
                            onEvent(ProfileEvent.UpdatePassword.Success)
                            _overlayState.update { Overlay.NONE }
                        }
                        .onFailure { throwable ->
                            _inputState.reduce {
                                copy(
                                    oldPassword = ValidatableInput(),
                                    newPassword = ValidatableInput()

                                )
                            }
                            onEvent(Failure(throwable.toRemoteFailure()))
                        }
                        .toScreenState()
                    _profileActionResult.reduce { copy(updatePassword = screenState) }
                }

                is ProfileAction.OnPhotoPicker.Launcher -> {
                    _overlayState.update { Overlay.IMAGE_PICKER }
                }

                is ProfileAction.OnPhotoPicker.Select -> {
                    val byteArray = uriConverter.toByteArray(action.uri)
                    _profileActionResult.reduce { copy(updateAvatarLoading = true) }
                    if (byteArray == null) {
                        Timber.e("Byte array is null")
                        onEvent(ProfileEvent.UpdateAvatar.Failure)
                    } else {
                        uploadAvatarUseCase(byteArray)
                            .onSuccess {
                                _profileActionResult.reduce { copy(updateAvatarLoading = false) }
                                onEvent(ProfileEvent.UpdateAvatar.Success)
                            }
                            .onFailure {
                                _profileActionResult.reduce { copy(updateAvatarLoading = false) }
                                onEvent(ProfileEvent.UpdateAvatar.Failure)
                            }
                    }

                }

                ProfileAction.OnEditDialogOpen -> {
                    _overlayState.update { Overlay.OPTIONS }
                }
            }
        }
    }

    @Immutable
    data class InputState(
        val newPassword: ValidatableInput = ValidatableInput(),
        val oldPassword: ValidatableInput = ValidatableInput(),
    )
}
package com.nhuhuy.replee.feature_auth.presentation.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.design_system.component.ValidatableInput
import com.nhuhuy.replee.feature_auth.domain.usecase.LoginWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Immutable
data class LoginState(
    val email: ValidatableInput = ValidatableInput(),
    val password: ValidatableInput = ValidatableInput(),
    val showLoading: Boolean = false
) : UiState {
    val inputValid: Boolean
        get() {
            return email.valid && password.valid
        }
}

sealed interface LoginAction : UiAction {
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    data object NavigateToSignUp : LoginAction
    data object NavigateToRecover : LoginAction
    data object Login : LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validator: Validator,
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
) : BaseViewModel<LoginAction, LoginEvent, LoginState>() {
    private val _state = MutableStateFlow(LoginState())
    override val state: StateFlow<LoginState>
        get() = _state.asStateFlow()

    override fun onAction(action: LoginAction) {
        viewModelScope.launch {
            when (action) {
                is LoginAction.OnEmailChanged -> {
                    _state.reduce {
                        copy(
                            email = ValidatableInput(
                                text = action.email,
                                validateResult = validator.validateEmail(action.email)
                            )
                        )
                    }
                }

                is LoginAction.OnPasswordChanged -> {
                    _state.reduce {
                        copy(
                            password = ValidatableInput(
                                text = action.password,
                                validateResult = validator.validatePassword(action.password)
                            )
                        )
                    }
                }

                LoginAction.Login -> {
                    val value = state.value
                    _state.reduce { copy(showLoading = true) }
                    loginWithEmailUseCase(value.email.text, value.password.text)
                        .onSuccess {
                            _state.reduce { copy(showLoading = false) }
                            onEvent(LoginEvent.NavigateToHome)
                        }
                        .onFailure { throwable ->
                            _state.reduce { copy(showLoading = false) }
                            onEvent(LoginEvent.Failure(throwable.toRemoteFailure()))
                        }
                }

                LoginAction.NavigateToRecover -> {
                    onEvent(LoginEvent.NavigateToRecover)
                }

                LoginAction.NavigateToSignUp -> {
                    onEvent(LoginEvent.NavigateToSignUp)
                }

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("LoginViewModel cleared")
    }
}
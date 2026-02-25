package com.nhuhuy.replee.feature_auth.presentation.sign_up

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.data.model.ValidateResult
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.design_system.component.ValidatableInput
import com.nhuhuy.replee.feature_auth.domain.usecase.SignUpWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Immutable
data class SignUpState(
    val name: ValidatableInput = ValidatableInput(),
    val email: ValidatableInput = ValidatableInput(),
    val password: ValidatableInput = ValidatableInput(),
    val confirmPassword: ValidatableInput = ValidatableInput(),
    val showLoading: Boolean = false,
) : UiState {
    val inputValid: Boolean
        get() {
            return name.valid && email.valid && password.valid && confirmPassword.valid
        }
}

sealed interface SignUpAction : UiAction {
    data object NavigateBack : SignUpAction
    data class OnNameChange(val name: String) : SignUpAction
    data class OnEmailChange(val email: String) : SignUpAction
    data class OnPasswordChange(val password: String) : SignUpAction
    data class OnConfirmPasswordChange(val confirmPassword: String) : SignUpAction
    data object SignUp : SignUpAction
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase
) : BaseViewModel<SignUpAction, SignUpEvent, SignUpState>() {
    private val _state = MutableStateFlow(SignUpState())
    override val state: StateFlow<SignUpState>
        get() = _state.asStateFlow()

    override fun onAction(action: SignUpAction) {
        viewModelScope.launch {
            when (action) {
                SignUpAction.NavigateBack -> {
                    onEvent(SignUpEvent.NavigateBack)
                }

                is SignUpAction.OnConfirmPasswordChange -> {
                    val password = state.value.password
                    _state.reduce {
                        copy(
                            confirmPassword = ValidatableInput(
                                text = action.confirmPassword,
                                validateResult = inputValidator.isPasswordConfirmed(
                                    password = password.text,
                                    confirmedPassword = action.confirmPassword
                                )
                            )
                        )
                    }
                }

                is SignUpAction.OnEmailChange -> {
                    _state.reduce {
                        copy(
                            email = ValidatableInput(
                                text = action.email,
                                validateResult = inputValidator.validateEmail(action.email)
                            )
                        )
                    }
                }

                is SignUpAction.OnNameChange -> {
                    _state.reduce {
                        copy(
                            name = ValidatableInput(
                                text = action.name,
                                validateResult = ValidateResult.Valid
                            )
                        )
                    }
                }

                is SignUpAction.OnPasswordChange -> {
                    _state.reduce {
                        copy(
                            password = ValidatableInput(
                                text = action.password,
                                validateResult = inputValidator.validatePassword(action.password)
                            ),
                        )
                    }
                }

                SignUpAction.SignUp -> {
                    val value = state.value
                    _state.reduce { copy(showLoading = true) }
                    signUpWithEmailUseCase(
                        name = value.name.text,
                        email = value.email.text,
                        password = value.password.text
                    )
                        .onSuccess {
                            _state.reduce { copy(showLoading = false) }
                            onEvent(SignUpEvent.SignUpSuccessfully)
                        }
                        .onFailure { throwable ->
                            _state.reduce { copy(showLoading = false) }
                            onEvent(SignUpEvent.Failure(throwable.toRemoteFailure()))
                        }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("SignUpViewModel cleared")
    }
}
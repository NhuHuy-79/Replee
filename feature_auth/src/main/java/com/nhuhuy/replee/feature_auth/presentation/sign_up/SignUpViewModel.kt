package com.nhuhuy.replee.feature_auth.presentation.sign_up

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.core.common.data.model.ValidateResult
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.design_system.component.DynamicInput
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Immutable
data class SignUpState(
    val name: DynamicInput = DynamicInput(),
    val email: DynamicInput = DynamicInput(),
    val password: DynamicInput = DynamicInput(),
    val confirmPassword: DynamicInput = DynamicInput(),
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
    private val validator: Validator,
    private val authRepository: AuthRepository
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
                            confirmPassword = DynamicInput(
                                text = action.confirmPassword,
                                validateResult = validator.isPasswordConfirmed(
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
                            email = DynamicInput(
                                text = action.email,
                                validateResult = validator.validateEmail(action.email)
                            )
                        )
                    }
                }

                is SignUpAction.OnNameChange -> {
                    _state.reduce {
                        copy(
                            name = DynamicInput(
                                text = action.name,
                                validateResult = ValidateResult.Valid
                            )
                        )
                    }
                }

                is SignUpAction.OnPasswordChange -> {
                    _state.reduce {
                        copy(
                            password = DynamicInput(
                                text = action.password,
                                validateResult = validator.validatePassword(action.password)
                            ),
                        )
                    }
                }

                SignUpAction.SignUp -> {
                    val value = state.value
                    _state.reduce { copy(showLoading = true) }
                    authRepository.signUpWithEmail(
                        name = value.name.text,
                        email = value.email.text,
                        password = value.password.text
                    )
                        .onSuccess {
                            _state.reduce { copy(showLoading = false) }
                            onEvent(SignUpEvent.SignUpSuccessfully)
                        }
                        .onFailure { error ->
                            _state.reduce { copy(showLoading = false) }
                            onEvent(SignUpEvent.Failure(error))
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
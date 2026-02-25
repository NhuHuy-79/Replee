package com.nhuhuy.replee.feature_auth.presentation.recover_password

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.nhuhuy.core.domain.model.onFailure
import com.nhuhuy.core.domain.model.onSuccess
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.design_system.component.ValidatableInput
import com.nhuhuy.replee.feature_auth.domain.usecase.SendRecoveryEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Immutable
data class RecoveryPasswordState(
    val email: ValidatableInput = ValidatableInput(),
    val showLoading: Boolean = false,
) : UiState {
    val inputValid: Boolean get() = email.valid
}

sealed interface RecoverPasswordAction : UiAction {
    data class OnEmailChange(val email: String) : RecoverPasswordAction

    data object OnSubmit : RecoverPasswordAction
    data object OnBack : RecoverPasswordAction
}

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val sendRecoveryEmailUseCase: SendRecoveryEmailUseCase,
) : BaseViewModel<RecoverPasswordAction, RecoverPasswordEvent, RecoveryPasswordState>() {
    private val _state = MutableStateFlow(RecoveryPasswordState())
    override val state: StateFlow<RecoveryPasswordState>
        get() = _state.asStateFlow()

    override fun onAction(action: RecoverPasswordAction) {
        viewModelScope.launch {
            when (action) {
                RecoverPasswordAction.OnBack -> {
                    onEvent(RecoverPasswordEvent.NavigateBack)
                }

                RecoverPasswordAction.OnSubmit -> {
                    val email = state.value.email
                    _state.reduce { copy(showLoading = true) }

                    sendRecoveryEmailUseCase(email.text)
                        .onSuccess {
                            _state.reduce { copy(showLoading = false, email = ValidatableInput()) }
                            onEvent(RecoverPasswordEvent.SendEmailSuccessfully)
                        }
                        .onFailure { throwable ->
                            _state.reduce { copy(showLoading = false) }
                            onEvent(RecoverPasswordEvent.Failure(throwable.toRemoteFailure()))

                        }

                }

                is RecoverPasswordAction.OnEmailChange -> {
                    _state.reduce {
                        copy(
                            email = ValidatableInput(
                                text = action.email,
                                validateResult = inputValidator.validateEmail(action.email)
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("RecoverPasswordViewModel cleared")
    }
}
package com.nhuhuy.replee.feature_auth.presentation.recover_password

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
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
data class RecoveryPasswordState(
    val email: DynamicInput = DynamicInput(),
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
    private val validator: Validator,
    private val authRepository: AuthRepository,
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
                    authRepository.sendRecoverPasswordEmail(email.text)
                        .onSuccess {
                            _state.reduce { copy(showLoading = false) }
                            onEvent(RecoverPasswordEvent.SendEmailSuccessfully)
                        }
                        .onFailure { error ->
                            _state.reduce { copy(showLoading = false) }
                            onEvent(RecoverPasswordEvent.Failure(error))

                        }
                }
                is RecoverPasswordAction.OnEmailChange -> {
                    _state.reduce {
                        copy(
                            email = DynamicInput(
                                text = action.email,
                                validateResult = validator.validateEmail(action.email)
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
package com.nhuhuy.replee.feature_auth.presentation.recover_password

import com.nhuhuy.replee.core.common.base.UiEvent
import com.nhuhuy.replee.core.common.data.model.RemoteFailure

sealed interface RecoverPasswordEvent : UiEvent{
    data object SendEmailSuccessfully : RecoverPasswordEvent
    data object NavigateBack : RecoverPasswordEvent
    data class Failure(val error: RemoteFailure): RecoverPasswordEvent
}
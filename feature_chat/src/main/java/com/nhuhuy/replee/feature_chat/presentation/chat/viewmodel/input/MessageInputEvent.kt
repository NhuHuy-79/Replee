package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input

import com.nhuhuy.replee.core.common.base.UiEvent

sealed interface MessageInputEvent : UiEvent {
    sealed interface FileValidateError : MessageInputEvent {
        data object Unsupported : FileValidateError
        data object FileTooLarge : FileValidateError
        data object Unknown : FileValidateError
    }
}
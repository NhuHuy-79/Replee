package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.input

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState

@Immutable
data class MessageInputState(
    val input: String = "",
) : UiState

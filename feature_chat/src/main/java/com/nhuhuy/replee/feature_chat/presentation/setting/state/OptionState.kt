package com.nhuhuy.replee.feature_chat.presentation.setting.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.nhuhuy.replee.core.common.base.UiState

@Immutable
data class OptionState(
    val otherUserName: String = "",
    val otherUserId: String = "",
    val otherUserEmail: String = "",
) : UiState

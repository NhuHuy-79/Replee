package com.nhuhuy.replee.feature_chat.presentation.information.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState

@Immutable
data class InformationState(
    val otherUserName: String = "",
    val otherUserId: String = "",
    val otherUserEmail: String = "",
) : UiState

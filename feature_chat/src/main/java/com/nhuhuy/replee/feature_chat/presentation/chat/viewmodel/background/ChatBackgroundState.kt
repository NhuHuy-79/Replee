package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.model.account.Account

@Immutable
data class ChatBackgroundState(
    val currentAccount: Account = Account(),
    val otherAccount: Account = Account(),
    val isBlocked: Boolean = false,
    val ownerIsBlocked: Boolean = false,
    val otherLastReadingTime: Long = 0L,
    val typingUserIds: List<String> = emptyList(),
) : UiState

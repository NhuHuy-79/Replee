package com.nhuhuy.replee.feature_chat.presentation.chat.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor

@Immutable
data class ChatState(
    val currentUserId: String = "",
    val messageInput: String = "",
    val otherUser: Account = Account(),
    val otherUserName: String = "",
    val seedColor: SeedColor = SeedColor.DEFAULT,
    val sendMessageState: ScreenState<String> = ScreenState.Idle
) : UiState

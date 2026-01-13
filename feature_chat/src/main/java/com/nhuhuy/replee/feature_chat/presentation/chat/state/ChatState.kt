package com.nhuhuy.replee.feature_chat.presentation.chat.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.feature_chat.domain.model.Message

@Immutable
data class ChatState(
    val currentUserId: String = "",
    val messageInput: String = "",
    val otherUser: Account = Account(),
    val otherUserName: String = "",
    val sendMessageState: ScreenState<Message> = ScreenState.Idle
) : UiState

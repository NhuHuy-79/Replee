package com.nhuhuy.replee.feature_chat.presentation.chat.state


import androidx.compose.runtime.Immutable
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.feature_chat.domain.model.message.Message

@Immutable
data class ChatState(
    val currentMessage: Message? = null,
    val isBlocked: Boolean = false,
    val currentUserId: String = "",
    val messageInput: String = "",
    val otherUser: Account = Account(),
    val otherUserName: String = "",
    val isReplying: Boolean = false,
    val overlay: ChatOverlay = ChatOverlay.None,
) : UiState


sealed interface ChatOverlay {
    data object None : ChatOverlay
    data object MessageOption : ChatOverlay
    data class FullImage(val url: String) : ChatOverlay
}
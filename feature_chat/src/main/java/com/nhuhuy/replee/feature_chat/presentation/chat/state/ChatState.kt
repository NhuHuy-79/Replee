package com.nhuhuy.replee.feature_chat.presentation.chat.state


import androidx.compose.runtime.Immutable
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.common.base.UiState

@Immutable
data class ChatState(
    val isBlocked: Boolean = false,
    val currentUserId: String = "",
    val messageInput: String = "",
    val otherUser: Account = Account(),
    val prefetchIndexKey: String? = null,
    val otherUserName: String = "",
    val dialog: ChatDialog = ChatDialog.None,
) : UiState


sealed interface ChatDialog {
    data object None : ChatDialog
    data class FullImage(val url: String) : ChatDialog
}
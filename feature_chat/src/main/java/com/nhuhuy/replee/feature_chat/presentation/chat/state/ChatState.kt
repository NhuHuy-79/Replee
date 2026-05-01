package com.nhuhuy.replee.feature_chat.presentation.chat.state


import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.chat.Message

@Immutable
data class ChatState(
    val currentMessage: Message? = null,
    val isBlocked: Boolean = false,
    val currentUserId: String = "",
    val currentUser: Account = Account(),
    val messageInput: String = "",
    val messageAnchorId: String = "",
    val messagePosition: Int = 0,
    val otherUser: Account = Account(),
    val otherUserName: String = "",
    val isReplying: Boolean = false,
    val overlay: ChatOverlay = ChatOverlay.None,
    val anchorToScroll: Anchor? = null,
    val isInitialJumpLoading: Boolean = false
) : UiState

@Immutable
data class Anchor(
    val lastTime: Long,
    val messageId: String
)

sealed interface ChatOverlay {
    data object None : ChatOverlay
    data class MessageOption(val message: Message) : ChatOverlay
    data class FullImage(val url: String) : ChatOverlay
    data object EmojiPicker : ChatOverlay
    data class MessageReaction(val message: Message) : ChatOverlay
}

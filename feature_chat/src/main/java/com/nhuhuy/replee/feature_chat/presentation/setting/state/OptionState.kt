package com.nhuhuy.replee.feature_chat.presentation.setting.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.design_system.component.DynamicInput
import com.nhuhuy.replee.feature_chat.domain.model.Conversation

@Immutable
data class OptionState(
    val currentConversation: Conversation = Conversation(),
    val otherUserName: String = "",
    val otherUserId: String = "",
    val otherUserEmail: String = "",
    val overlay: OptionOverlay = OptionOverlay.NONE,
    val ownerNickName: DynamicInput = DynamicInput(),
    val otherUserNickName: DynamicInput = DynamicInput(),
) : UiState

enum class OptionOverlay {
    NONE,
    SET_NICK_NAME,
    DELETE_CHAT,
}
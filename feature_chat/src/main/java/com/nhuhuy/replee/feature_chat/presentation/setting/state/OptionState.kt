package com.nhuhuy.replee.feature_chat.presentation.setting.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.design_system.component.ValidatableInput
import com.nhuhuy.replee.core.design_system.state.ScreenState
import com.nhuhuy.replee.feature_chat.domain.model.Conversation

@Immutable
data class OptionState(
    val changeNickNameResult: ScreenState<Unit> = ScreenState.Idle,
    val currentConversation: Conversation = Conversation(),
    val otherUserName: String = "",
    val otherUserId: String = "",
    val otherUserEmail: String = "",
    val otherUserImg: String = "",
    val overlay: OptionOverlay = OptionOverlay.NONE,
    val ownerNickName: ValidatableInput = ValidatableInput(),
    val otherUserNickName: ValidatableInput = ValidatableInput(),
) : UiState

enum class OptionOverlay {
    NONE,
    SET_NICK_NAME,
    DELETE_CHAT,
}
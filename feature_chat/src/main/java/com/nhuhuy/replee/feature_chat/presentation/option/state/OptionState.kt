package com.nhuhuy.replee.feature_chat.presentation.option.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.ScreenState
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.data.data_store.SeedColor
import com.nhuhuy.replee.core.design_system.component.ValidatableInput
import com.nhuhuy.replee.feature_chat.domain.model.converastion.Conversation

@Immutable
data class OptionState(
    val changeNickNameResult: ScreenState<Unit> = ScreenState.Idle,
    val currentConversation: Conversation = Conversation(),
    val otherUserName: String = "",
    val otherUserId: String = "",
    val otherUserEmail: String = "",
    val otherUserImg: String = "",
    val overlay: OptionOverlay = OptionOverlay.NONE,
    val otherUserNickName: ValidatableInput = ValidatableInput(),
    val muteConversation: Boolean = false,
    val pinConversation: Boolean = false,
    val currentColor: SeedColor = SeedColor.SAPPHIRE
) : UiState

enum class OptionOverlay {
    NONE,
    SET_NICK_NAME,
    DELETE_CHAT,
    SELECT_COLOR
}
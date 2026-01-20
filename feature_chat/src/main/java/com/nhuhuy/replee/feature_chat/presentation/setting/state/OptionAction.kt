package com.nhuhuy.replee.feature_chat.presentation.setting.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SecondaryOption

sealed interface OptionAction : UiAction {
    data class OnNameChange(val name: String) : OptionAction
    data object OnBackPressed: OptionAction
    data class OnSecondaryOptionSelect(val secondaryOption: SecondaryOption) : OptionAction
    data object OnPin : OptionAction
    data object OnMute : OptionAction
    data object OnConversationDelete : OptionAction
    data object OnNameSet : OptionAction

    sealed interface ShowOverlay : OptionAction {
        data object Dismiss : OptionAction
        data class SetNickName(val user1: String, val user2: String) : ShowOverlay
        data object Block : ShowOverlay
        data object DeleteChat : ShowOverlay
    }
}


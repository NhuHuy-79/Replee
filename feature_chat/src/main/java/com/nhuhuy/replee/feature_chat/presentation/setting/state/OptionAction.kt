package com.nhuhuy.replee.feature_chat.presentation.setting.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SecondaryOption

sealed interface OptionAction : UiAction {
    data class OnOwnerNickNameChange(val name: String) : OptionAction
    data class OnOtherNickNameChange(val name: String) : OptionAction
    data object OnBackPressed: OptionAction
    data class OnSecondaryOptionSelect(val secondaryOption: SecondaryOption) : OptionAction
    data class OnPin(val enable: Boolean) : OptionAction
    data class OnMute(val enable: Boolean) : OptionAction
    data object OnConversationDelete : OptionAction
    data object OnOwnerNickNameSet : OptionAction
    data object OnOtherUserNickNameSet : OptionAction
    data object OnDismiss : OptionAction
}


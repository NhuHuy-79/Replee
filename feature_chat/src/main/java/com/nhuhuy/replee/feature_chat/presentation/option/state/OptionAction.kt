package com.nhuhuy.replee.feature_chat.presentation.option.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.data.data_store.SeedColor
import com.nhuhuy.replee.feature_chat.presentation.option.component.SecondaryOption

sealed interface OptionAction : UiAction {
    data class OnOtherNickNameChange(val name: String) : OptionAction
    data object OnBackPressed: OptionAction
    data class OnSecondaryOptionSelect(val secondaryOption: SecondaryOption) : OptionAction
    data class OnColorSelect(val themeColor: SeedColor) : OptionAction
    data object OnChatColorSet : OptionAction
    data class OnPin(val enable: Boolean) : OptionAction
    data class OnMute(val enable: Boolean) : OptionAction
    data object OnConversationDelete : OptionAction
    data object OnNickNameSet : OptionAction
    data object OnDismiss : OptionAction
}


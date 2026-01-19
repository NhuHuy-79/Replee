package com.nhuhuy.replee.feature_chat.presentation.setting.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.feature_chat.presentation.setting.component.Option
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SecondaryOption

sealed interface OptionAction : UiAction {
    data object OnBackPressed: OptionAction
    data class OnMainOptionSelect(val option: Option) : OptionAction
    data class OnSecondaryOptionSelect(val secondaryOption: SecondaryOption) : OptionAction
}
package com.nhuhuy.replee.feature_chat.presentation.information.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.feature_chat.presentation.information.component.Option

sealed interface InformationAction : UiAction {
    data object OnBackPressed: InformationAction
    data class OnOptionSelect(val option: Option) : InformationAction
}
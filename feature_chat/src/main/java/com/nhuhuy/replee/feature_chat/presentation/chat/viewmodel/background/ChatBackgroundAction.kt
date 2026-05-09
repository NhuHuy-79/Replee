package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.background

import com.nhuhuy.replee.core.common.base.UiAction

sealed interface ChatBackgroundAction : UiAction {
    data object OnBackClick : ChatBackgroundAction
    data object OnMoreClick : ChatBackgroundAction
    data object OnSearchClick : ChatBackgroundAction
    data object OnPinClick : ChatBackgroundAction
    data object OnUnblockUser : ChatBackgroundAction
}

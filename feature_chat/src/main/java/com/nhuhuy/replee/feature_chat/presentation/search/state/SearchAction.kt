package com.nhuhuy.replee.feature_chat.presentation.search.state

import com.nhuhuy.replee.core.common.base.UiAction

sealed interface SearchAction : UiAction {
    data class OnQueryChange(val query: String) : SearchAction
    data object OnSearch : SearchAction
}
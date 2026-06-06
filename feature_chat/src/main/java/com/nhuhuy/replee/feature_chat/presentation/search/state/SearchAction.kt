package com.nhuhuy.replee.feature_chat.presentation.search.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.model.chat.Message

sealed interface SearchAction : UiAction {
    data class OnQueryChange(val query: String) : SearchAction
    data object OnSearch : SearchAction
    data object OnSearchClose : SearchAction
    data object OnNavigateBack : SearchAction
    data class OnMessagePress(val message: Message) : SearchAction
}

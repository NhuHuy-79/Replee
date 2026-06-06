package com.nhuhuy.replee.feature_home.presentation.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.SearchHistoryResult

sealed interface HomeAction : UiAction {
    data object OnSearch : HomeAction
    data object OnSearchBarClose : HomeAction
    data class OnQueryChange(val value: String) : HomeAction

    data object OnMessageLongPress : HomeAction

    data object OnImagePress : HomeAction

    data class OnExpandChange(val expand: Boolean) : HomeAction
    data object OnDismissPress : HomeAction
    data object OnAddFabClick : HomeAction
    data class OnHomeClick(val conversation: Conversation) : HomeAction
    data object Retry : HomeAction

    data object OnOwnerClick : HomeAction
    data class OnAvatarClick(val account: Account) : HomeAction
    data class OnSearchResultClick(
        val historyResult: SearchHistoryResult
    ) : HomeAction
}

package com.nhuhuy.replee.feature_home.presentation.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.SearchHistoryResult

sealed interface ConversationAction : UiAction{
    data object OnSearch: ConversationAction
    data object OnSearchBarClose: ConversationAction
    data class OnQueryChange(val value: String) : ConversationAction

    data object OnMessageLongPress : ConversationAction

    data object OnImagePress : ConversationAction

    data class OnExpandChange(val expand: Boolean) : ConversationAction
    data object OnDismissPress: ConversationAction
    data object OnAddFabClick: ConversationAction
    data class OnConversationClick(val conversation : Conversation) : ConversationAction
    data object Retry: ConversationAction

    data object OnOwnerClick: ConversationAction
    data class OnAvatarClick(val account: Account): ConversationAction
    data class OnSearchResultClick(
        val historyResult: SearchHistoryResult
    ) : ConversationAction
}

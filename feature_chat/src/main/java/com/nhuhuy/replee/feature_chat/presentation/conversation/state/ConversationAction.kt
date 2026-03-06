package com.nhuhuy.replee.feature_chat.presentation.conversation.state

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.feature_chat.domain.model.Conversation

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
}
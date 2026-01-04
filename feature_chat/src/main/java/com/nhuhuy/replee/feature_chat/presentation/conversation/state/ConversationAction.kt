package com.nhuhuy.replee.feature_chat.presentation.conversation.state

import com.nhuhuy.replee.core.common.base.UiAction
import com.nhuhuy.replee.feature_chat.domain.model.Conversation

sealed interface ConversationAction : UiAction{
    data object OnSearchBarClick: ConversationAction
    data object OnSearchBarClose: ConversationAction
    data class OnQueryChange(val value: String) : ConversationAction

    data class OnExpandChange(val expand: Boolean) : ConversationAction
    data object OnDismissPress: ConversationAction
    data object OnAddFabClick: ConversationAction
    data class OnConversationClick(val conversationId: String) : ConversationAction
    data object Retry: ConversationAction
    data object OnAvatarClick: ConversationAction
}
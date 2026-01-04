package com.nhuhuy.replee.feature_chat.presentation.conversation.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.common.data.model.Account

@Immutable
data class ConversationState(
    val currentUser: Account = Account(),
    val userList: List<Account> = emptyList(),
    val expandSearchBar: Boolean = false,
    val searchQuery: String = "",
) : UiState

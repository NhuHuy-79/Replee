package com.nhuhuy.replee.feature_chat.presentation.search.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.model.account.Account

@Immutable
data class SearchState(
    val searchQuery: String = "",
    val currentUser: Account = Account(),
    val otherUser: Account = Account()
) : UiState

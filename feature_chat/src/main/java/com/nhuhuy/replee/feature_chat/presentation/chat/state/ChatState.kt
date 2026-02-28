package com.nhuhuy.replee.feature_chat.presentation.chat.state


import androidx.compose.runtime.Immutable
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor

@Immutable
data class ChatState(
    val isBlocked: Boolean = false,
    val currentUserId: String = "",
    val messageInput: String = "",
    val otherUser: Account = Account(),
    val prefetchIndexKey: String? = null,
    val otherUserName: String = "",
    val seedColor: SeedColor = SeedColor.DEFAULT
) : UiState

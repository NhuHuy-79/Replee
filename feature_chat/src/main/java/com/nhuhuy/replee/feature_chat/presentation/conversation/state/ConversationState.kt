package com.nhuhuy.replee.feature_chat.presentation.conversation.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.core.design_system.state.ScreenState

@Immutable
data class ConversationState(
    val searchState: ScreenState<List<Account>> = ScreenState.Idle,
    val currentUser: Account = Account(),
    val synchronizingState: SynchronizingState = SynchronizingState.NONE,
    val expandSearchBar: Boolean = false,
    val searchQuery: String = "",
    val bottomSheet: BottomSheet = BottomSheet.CLOSE
) : UiState

enum class SynchronizingState{
    NONE,
    SYNC,
    FAILURE
}

enum class BottomSheet{
    CLOSE,
    OPEN,
}
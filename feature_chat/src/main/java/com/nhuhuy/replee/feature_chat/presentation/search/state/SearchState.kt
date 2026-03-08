package com.nhuhuy.replee.feature_chat.presentation.search.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState

@Immutable
data class SearchState(
    val query: String = "",
) : UiState

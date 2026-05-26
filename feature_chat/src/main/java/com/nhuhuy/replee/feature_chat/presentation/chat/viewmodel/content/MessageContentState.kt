package com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.content

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.common.base.UiState
import com.nhuhuy.replee.feature_chat.presentation.chat.viewmodel.main.ChatOverlay

@Immutable
data class MessageContentState(
    val pageSize: Int = 20,
    val anchorMessagePosition: Int = 0,
    val anchorMessageId: String? = null,
    val isLoadingTop: Boolean = false,
    val isLoadingBottom: Boolean = false,
    val thresholdTrigger: Int = 5,
    val endOfBottom: Boolean = false,
    val endOfTop: Boolean = false,
    val overlay: ChatOverlay = ChatOverlay.None,
    val jumpTrigger: Long = 0L,
) : UiState

enum class ScrollPosition {
    MIDDLE, TOP, BOTTOM
}

package com.nhuhuy.replee.feature_chat.presentation.chat.state

import androidx.compose.runtime.Immutable
import com.nhuhuy.replee.core.model.chat.LocalPathMessage

@Immutable
sealed class MessageUiModel {
    data class MessageItem(
        val message: LocalPathMessage,
        val position: MessagePosition = MessagePosition.SINGLE,
        val isLastInGroup: Boolean = false
    ) : MessageUiModel()

    data class DateSeparator(val date: String) : MessageUiModel()
}

enum class MessagePosition {
    START, MIDDLE, END, SINGLE
}

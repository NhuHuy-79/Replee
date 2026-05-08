package com.nhuhuy.replee.feature_chat.utils

import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.state.MessageUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun List<LocalPathMessage>.toUiModelsWithSeparators(): List<MessageUiModel> {
    if (this.isEmpty()) return emptyList()

    val result = mutableListOf<MessageUiModel>()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    for (i in this.indices) {
        val beforeItem = this[i]
        result.add(MessageUiModel.MessageItem(beforeItem))

        val beforeDate = beforeItem.message.sentAt.toLocalDate()
        val afterItem = this.getOrNull(i + 1)

        if (afterItem == null) {
            result.add(MessageUiModel.DateSeparator(beforeDate.format(formatter)))
        } else {
            val afterDate = afterItem.message.sentAt.toLocalDate()
            if (beforeDate != afterDate) {
                result.add(MessageUiModel.DateSeparator(beforeDate.format(formatter)))
            }
        }
    }

    return result
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}
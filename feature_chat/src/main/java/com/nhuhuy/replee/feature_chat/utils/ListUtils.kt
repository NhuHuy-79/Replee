package com.nhuhuy.replee.feature_chat.utils

import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.MessageType
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessagePosition
import com.nhuhuy.replee.feature_chat.presentation.chat.model.MessageUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun List<LocalPathMessage>.toUiModelsWithSeparators(): List<MessageUiModel> {
    if (this.isEmpty()) return emptyList()
    val result = mutableListOf<MessageUiModel>()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    for (i in this.indices) {
        val currentItem = this[i]
        val olderItem = this.getOrNull(i + 1)
        val newerItem = this.getOrNull(i - 1)

        val currentIsText = currentItem.message.type == MessageType.TEXT

        val isFirstInGroup = when {
            olderItem == null || !currentIsText -> true
            else -> {
                val olderIsText = olderItem.message.type == MessageType.TEXT
                val isDifferentSender = olderItem.message.senderId != currentItem.message.senderId
                val isFarApart = (currentItem.message.sentAt - olderItem.message.sentAt) > 600_000L
                val isDifferentDate =
                    currentItem.message.sentAt.toLocalDate() != olderItem.message.sentAt.toLocalDate()
                !olderIsText || isDifferentSender || isFarApart || isDifferentDate
            }
        }

        val isLastInGroup = when {
            newerItem == null || !currentIsText -> true
            else -> {
                val newerIsText = newerItem.message.type == MessageType.TEXT
                val isDifferentSender = newerItem.message.senderId != currentItem.message.senderId
                val isFarApart = (newerItem.message.sentAt - currentItem.message.sentAt) > 600_000L
                val isDifferentDate =
                    newerItem.message.sentAt.toLocalDate() != currentItem.message.sentAt.toLocalDate()
                !newerIsText || isDifferentSender || isFarApart || isDifferentDate
            }
        }

        val position = when {
            isFirstInGroup && isLastInGroup -> MessagePosition.SINGLE
            isFirstInGroup -> MessagePosition.START
            isLastInGroup -> MessagePosition.END
            else -> MessagePosition.MIDDLE
        }

        result.add(
            MessageUiModel.MessageItem(
                data = currentItem,
                position = position,
                isLastInGroup = isLastInGroup
            )
        )

        // Thêm Date Separator nếu ngày của tin nhắn tiếp theo (older) khác ngày hiện tại
        val currentDate = currentItem.message.sentAt.toLocalDate()
        val nextItemDate = olderItem?.message?.sentAt?.toLocalDate()

        if (nextItemDate == null || currentDate != nextItemDate) {
            result.add(MessageUiModel.DateSeparator(currentDate.format(formatter)))
        }
    }

    return result
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}
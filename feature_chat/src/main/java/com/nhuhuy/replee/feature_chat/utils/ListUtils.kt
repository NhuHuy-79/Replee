package com.nhuhuy.replee.feature_chat.utils

import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.feature_chat.presentation.chat.state.MessagePosition
import com.nhuhuy.replee.feature_chat.presentation.chat.state.MessageUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun List<LocalPathMessage>.toUiModelsWithSeparators(): List<MessageUiModel> {
    if (this.isEmpty()) return emptyList()

    val result = mutableListOf<MessageUiModel>()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Giả định list đầu vào đã được sắp xếp DESC (Mới -> Cũ)
    for (i in this.indices) {
        val currentItem = this[i]
        val olderItem = this.getOrNull(i + 1)
        val newerItem = this.getOrNull(i - 1)

        // 1. Tính toán logic Grouping
        val isFirstInGroup = when (olderItem) {
            null -> true
            else -> {
                val isDifferentSender = olderItem.message.senderId != currentItem.message.senderId
                val isFarApart = (currentItem.message.sentAt - olderItem.message.sentAt) > 600_000L
                val isDifferentDate =
                    currentItem.message.sentAt.toLocalDate() != olderItem.message.sentAt.toLocalDate()
                isDifferentSender || isFarApart || isDifferentDate
            }
        }

        val isLastInGroup = when (newerItem) {
            null -> true
            else -> {
                val isDifferentSender = newerItem.message.senderId != currentItem.message.senderId
                val isFarApart = (newerItem.message.sentAt - currentItem.message.sentAt) > 600_000L
                val isDifferentDate =
                    newerItem.message.sentAt.toLocalDate() != currentItem.message.sentAt.toLocalDate()
                isDifferentSender || isFarApart || isDifferentDate
            }
        }

        val position = when {
            isFirstInGroup && isLastInGroup -> MessagePosition.SINGLE
            isFirstInGroup -> MessagePosition.START
            isLastInGroup -> MessagePosition.END
            else -> MessagePosition.MIDDLE
        }

        // 2. Thêm MessageItem vào list
        result.add(
            MessageUiModel.MessageItem(
                data = currentItem,
                position = position,
                isLastInGroup = isLastInGroup
            )
        )

        // 3. Logic thêm DateSeparator (nếu là tin nhắn cũ nhất trong ngày hoặc cuối list)
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
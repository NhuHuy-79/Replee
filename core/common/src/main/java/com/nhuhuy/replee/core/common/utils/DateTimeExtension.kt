package com.nhuhuy.replee.core.common.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

fun Long.toLocalDate(
    zoneId: ZoneId = ZoneId.systemDefault()
): LocalDate =
    Instant.ofEpochMilli(this)
        .atZone(zoneId)
        .toLocalDate()


fun Long.formatToString(
    pattern: String = "dd/MM/yyyy",
): String? {
    val date = this.toLocalDate()
    return date.format(DateTimeFormatter.ofPattern(pattern))
}


fun Long.formatToChatTime(
    locale: Locale = Locale.getDefault()
): String {
    val messageTime = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.systemDefault()
    )
    val now = ZonedDateTime.now(ZoneId.systemDefault())

    val messageDate = messageTime.toLocalDate()
    val nowDate = now.toLocalDate()

    val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
    val startOfCurrentWeek = nowDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))

    return when {
        messageDate.isEqual(nowDate) -> {
            messageTime.format(DateTimeFormatter.ofPattern("HH:mm", locale))
        }

        (messageDate.isEqual(startOfCurrentWeek) || messageDate.isAfter(startOfCurrentWeek))
                && messageDate.isBefore(nowDate) -> {
            messageTime.format(DateTimeFormatter.ofPattern("EEEE HH:mm", locale))
        }

        messageTime.year == now.year -> {
            messageTime.format(DateTimeFormatter.ofPattern("dd MMM", locale))
        }

        else -> {
            messageTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", locale))
        }
    }
}
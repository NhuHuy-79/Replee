package com.nhuhuy.replee.core.common.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

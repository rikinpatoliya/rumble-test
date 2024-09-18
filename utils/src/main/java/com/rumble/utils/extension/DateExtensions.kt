package com.rumble.utils.extension

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun LocalDateTime.getMediumDateTimeString(): String =
    this.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH))

fun LocalDateTime.getDateString(): String =
    this.format(DateTimeFormatter.ofPattern("MMM dd", Locale.US))

fun LocalDateTime.getDateShortMonthString(): String =
    this.month.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.US)

fun LocalDateTime.getTimeString(withSpace: Boolean = true): String =
    this.format(DateTimeFormatter.ofPattern(if (withSpace) "hh:mm a" else "hh:mma", Locale.US))

fun LocalDate.toUtcLong(): Long {
    return this.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000

}
package com.rumble.utils.extension

import android.content.Context
import com.rumble.utils.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun Long.shortString(withDecimal: Boolean = true): String =
    when (this) {
        in 0..999 -> this.toString()
        in 1_000..999_999 -> getRightNumber(withDecimal, this, 1_000, "K")
        in 1_000_000..999_999_999 -> getRightNumber(withDecimal, this, 1_000_000, "M")
        else -> getRightNumber(withDecimal, this, 1_000_000_000, "B")
    }

private fun getRightNumber(withDecimal: Boolean, value: Long, divider: Int, suffix: String): String {
    val mainNumber = value / divider.toFloat()
    val leftover = (mainNumber - (value / divider)).toBigDecimal().setScale(1, RoundingMode.FLOOR)
    return if (withDecimal && leftover > BigDecimal.ZERO) {
        "${mainNumber.toBigDecimal().setScale(0, RoundingMode.FLOOR) + leftover}$suffix"
    } else {
        "${mainNumber.toInt()}$suffix"
    }
}

fun Long.parsedTime(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.SECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours)
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

fun Long.videoRecordTimerTime() =
    String.format(
        "%02d:%02d",
        TimeUnit.NANOSECONDS.toMinutes(this),
        TimeUnit.NANOSECONDS.toSeconds(this) % 60
    )

fun Long.videoTrimTimerTime(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return if (minutes > 9) {
        String.format("%02d:%02d", minutes, seconds)
    } else {
        String.format("%01d:%02d", minutes, seconds)
    }
}

fun Long.convertToDate(pattern: String, useUtc: Boolean = false): String {
    val date = Date(this)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    if (useUtc) format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(date)
}

fun Long.toUtcLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.of("UTC")).toLocalDate()
}

fun Long.toUtcLocalMilliseconds(): Long {
    val utcInstant = Instant.ofEpochMilli(this)
    val newMillisLocal = this + TimeZone.getDefault().getOffset(utcInstant.toEpochMilli())
    val newLocalInstant = Instant.ofEpochMilli(newMillisLocal)
    return newLocalInstant.toEpochMilli()
}

fun Long.liveDurationString(context: Context): String {
    val thisInSeconds = TimeUnit.MILLISECONDS.toSeconds(this)
    val days = TimeUnit.MILLISECONDS.toDays(this).toInt()
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = ((thisInSeconds - (hours * 60 * 60)) / 60).toInt()
    val seconds = (thisInSeconds - (hours * 60 * 60) - (minutes * 60)).toInt()
    return if (hours >= 100) context.resources.getQuantityString(R.plurals.days_ago, days, days)
    else String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun Long.toChannelIdString(): String {
    return "_c${this}"
}

fun Long.toUserIdString(): String {
    return "_u${this.toString(36)}"
}

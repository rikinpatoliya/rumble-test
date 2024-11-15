package com.rumble.utils.extension

import android.content.Context
import android.view.Surface
import timber.log.Timber
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Int.utcSecondTimestampToLocal(): LocalDateTime {
    val utc = LocalDateTime.ofEpochSecond(this.toLong(), 0, ZoneOffset.UTC)
    val utcZoned = utc.atZone(ZoneOffset.UTC)
    val localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault())
    return localZoned.toLocalDateTime()
}

/**
 * Convert Int to string in short format, e.g. 1.2K, 1.2K, 1.2B
 */
fun Int.shortString(withDecimal: Boolean = true): String = this.toLong().shortString(withDecimal)

/**
 * Converts duration in seconds to string in format HH:MM:SS or MM:SS
 */
fun Int.durationString(): String {
    val hours = TimeUnit.SECONDS.toHours(this.toLong())
    val minutes = TimeUnit.SECONDS.toMinutes(this.toLong()) - TimeUnit.HOURS.toMinutes(hours)
    val seconds =
        TimeUnit.SECONDS.toSeconds(this.toLong()) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(
            hours
        )
    val str = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
    return str
}

/*
* Converting user id to base36 and adding _u prefix
* */
fun Int.toUserIdString(): String {
    return "_u${this.toString(36)}"
}

/*
* Converting base36 user id to integer and removing _u prefix
* */
fun String.toUserIdInt(): Int {
    return this.removePrefix("_u").toInt(36)
}

fun Int.toChannelIdString(): String {
    return "_c${this}"
}

fun Int.toLocalizedString(tag: String): String {
    var result = ""
    val locale: Locale = Locale.getDefault()
    try {
        result = NumberFormat.getInstance(locale).format(this)
    } catch (e: Exception) {
        Timber.tag(tag).e(e.localizedMessage ?: "Int.toLocalizedString error")
    }
    return result
}

fun Int.toRotationFloat(): Float = when (this) {
    Surface.ROTATION_90 -> 90F
    Surface.ROTATION_180 -> 180F
    Surface.ROTATION_270 -> 270F
    else -> 0F
}

fun Int.toPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

fun Int.toDp(context: Context): Int = (this / context.resources.displayMetrics.density).toInt()

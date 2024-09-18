package com.rumble.utils.extension

import android.content.Context
import com.rumble.utils.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

fun LocalDateTime.agoString(context: Context): String {
    val local = LocalDateTime.now()

    val period = Period.between(this.toLocalDate(), local.toLocalDate())
    return if (period.years > 0) {
        context.resources.getQuantityString(R.plurals.years_ago, period.years, period.years)
    } else if (period.months > 0) {
        context.resources.getQuantityString(R.plurals.months_ago, period.months, period.months)
    } else if (period.days > 0) {
        context.resources.getQuantityString(R.plurals.days_ago, period.days, period.days)
    } else if ((local.hour - this.hour) > 0) {
        val hours = local.hour - this.hour
        context.resources.getQuantityString(R.plurals.hours_ago, hours, hours)
    } else if ((local.minute - this.minute) > 0) {
        val minutes = local.minute - this.minute
        context.resources.getQuantityString(R.plurals.minutes_ago, minutes, minutes)
    } else if ((local.second - this.second) > 0) {
        val seconds = local.second - this.second
        context.resources.getQuantityString(R.plurals.seconds_ago, seconds, seconds)
    } else {
        context.resources.getString(R.string.now)
    }
}

fun LocalDateTime.simpleString(context: Context): String {
    val local = LocalDateTime.now()

    val period = Period.between(local.toLocalDate(), this.toLocalDate())
    return if (period.years > 0) {
        context.resources.getQuantityString(R.plurals.years, period.years, period.years)
    } else if (period.months > 0) {
        context.resources.getQuantityString(R.plurals.months, period.months, period.months)
    } else if (period.days > 0) {
        context.resources.getQuantityString(R.plurals.days, period.days, period.days)
    } else if ((this.hour - local.hour) > 0) {
        val hours = this.hour - local.hour
        context.resources.getQuantityString(R.plurals.hours, hours, hours)
    } else if ((this.minute - local.minute) > 0) {
        val minutes = this.minute - local.minute
        context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
    } else if ((this.second - local.second) > 0) {
        val seconds = this.second - local.second
        context.resources.getQuantityString(R.plurals.seconds, seconds, seconds)
    } else {
        context.resources.getString(R.string.now)
    }
}

fun LocalDate.numberOfYearsTillNow(): Int? {
    return try {
        val period = Period.between(this, LocalDate.now())
        return period.years
    } catch (e: Exception) {
        null
    }
}

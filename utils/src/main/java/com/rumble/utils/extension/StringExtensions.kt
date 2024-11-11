package com.rumble.utils.extension

import android.net.Uri
import androidx.compose.ui.graphics.Color
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun String.convertUtcToLocal(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .atOffset(ZoneOffset.UTC)
        .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
}

fun String.getComposeColor(): Color? =
    try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: java.lang.IllegalArgumentException) {
        null
    }

fun String.navigationSafeEncode(): String =
    Uri.encode(this)

fun String.navigationSafeDecode(): String =
    Uri.decode(this)

fun String.getChannelId(): Long = this.getNumericId()

fun String.getUserId(): Long = this.getNumericId()

fun String.getNumericId(): Long {
    return try {
        when {
            this.startsWith("_u") -> this.substring(2).toLong(36)
            this.startsWith("_") -> this.substring(2).toLong(10)
            else -> this.toLong(10)
        }
    } catch (e: NumberFormatException) {
        -1
    }
}

fun String.capitalizeWords(): String =
    this.split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

fun String.getEmoteName() =
    this.substring(this.indexOfFirst { it == ':' } + 1, this.lastIndexOf(":"))

fun String.extractPrice() =
    this.filter { it.isDigit() || it == '.' || it == ',' }

fun String.toDate(): LocalDate? {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        null
    }
}

fun String.insertTextAtPosition(text: String, position: Int) =
    if (position >= 0 && position <= this.length) {
        val buffer = StringBuffer(this)
        buffer.insert(position, text)
        buffer.toString()
    } else this

fun String.removeCharacterAtPosition(position: Int) =
    if (position >= 0 && position < this.length) {
        val buffer = StringBuffer(this)
        buffer.deleteCharAt(position)
        buffer.toString()
    } else this
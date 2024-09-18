package com.rumble.utils.extension

import java.math.BigDecimal
import java.text.DecimalFormat

fun BigDecimal.toCurrencyString(currencySymbol: String): String =
    currencySymbol + DecimalFormat().apply { minimumFractionDigits = 2 }.format(this)

fun BigDecimal.toRantCurrencyString(currencySymbol: String): String =
    currencySymbol + DecimalFormat().format(this)

fun BigDecimal.toPriceString(): String =
    DecimalFormat().apply { minimumFractionDigits = 2 }.format(this)
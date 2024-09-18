package com.rumble.utils.extension

import java.math.RoundingMode

fun Float.round(precision: Int): Float =
    this.toBigDecimal().setScale(precision, RoundingMode.HALF_EVEN).toFloat()
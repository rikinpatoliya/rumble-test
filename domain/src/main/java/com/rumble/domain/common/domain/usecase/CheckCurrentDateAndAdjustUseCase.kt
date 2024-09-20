package com.rumble.domain.common.domain.usecase

import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

class CheckCurrentDateAndAdjustUseCase @Inject constructor() {

    operator fun invoke(newUtcMillis: Long): Long {
        val utcNow = Instant.now()
        val currentMillis = utcNow.toEpochMilli()
        val currentUtcDate = utcNow.atZone(ZoneOffset.UTC).toLocalDate()
        val selectedDate = Instant.ofEpochMilli(newUtcMillis).atZone(ZoneOffset.UTC).toLocalDate()
        return if (selectedDate == currentUtcDate) {
            currentMillis
        } else {
            newUtcMillis
        }
    }
}
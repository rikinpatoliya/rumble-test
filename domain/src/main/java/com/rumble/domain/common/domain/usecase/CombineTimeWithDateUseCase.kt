package com.rumble.domain.common.domain.usecase

import android.content.ActivityNotFoundException
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

private const val TAG = "CombineTimeWithDateUseCase"

class CombineTimeWithDateUseCase @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {
    operator fun invoke(hour: Int, minute: Int, utcMillis: Long): Long {
        return try {
            val utcInstant = Instant.ofEpochMilli(utcMillis)
            val daysToAdjust = hour / 24
            val updatedHour = (hour + 24) % 24
            val updatedInstant = utcInstant.atZone(ZoneOffset.UTC)
                .plusDays(daysToAdjust.toLong())
                .withHour(updatedHour)
                .withMinute(minute)
                .toInstant()
            updatedInstant.toEpochMilli()
        } catch (e: ActivityNotFoundException) {
            unhandledErrorUseCase(TAG, e)
            utcMillis
        }
    }
}
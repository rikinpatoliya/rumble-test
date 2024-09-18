package com.rumble.domain.onboarding.domain.domainmodel

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromOnboardingType(value: String) = enumValueOf<OnboardingType>(value)

    @TypeConverter
    fun fromOnboardingType(value: OnboardingType) = value.name

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time?.toLong()

}
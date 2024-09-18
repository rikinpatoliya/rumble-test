package com.rumble.domain.onboarding.domain.domainmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "OnboardingView")
@TypeConverters(Converters::class)
data class RoomOnboardingView(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "onboardingType")
    val onboardingType: OnboardingType,
    @ColumnInfo(name = "version")
    val version: Int
)
package com.rumble.domain.onboarding.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingType
import com.rumble.domain.onboarding.domain.domainmodel.RoomOnboardingView

@Dao
interface OnboardingViewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(roomOnboardingView: RoomOnboardingView)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveList(roomOnboardingViewList: List<RoomOnboardingView>)

    @Query("SELECT * FROM OnboardingView where onboardingType = :onboardingType and version = :version")
    suspend fun get(onboardingType: OnboardingType, version: Int): RoomOnboardingView?

    @Query("SELECT * FROM OnboardingView")
    suspend fun getAll(): List<RoomOnboardingView>
}
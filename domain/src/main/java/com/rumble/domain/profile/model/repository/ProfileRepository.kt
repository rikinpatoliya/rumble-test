package com.rumble.domain.profile.model.repository

import android.net.Uri
import androidx.paging.PagingData
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.domain.domainmodel.GetUserProfileResult
import com.rumble.domain.settings.domain.domainmodel.GetUserUnreadNotificationsResult
import com.rumble.domain.settings.domain.domainmodel.UpdateUserProfileResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun signOut()
    suspend fun getUserProfile(): GetUserProfileResult
    suspend fun updateUserProfile(userProfileEntity: UserProfileEntity): UpdateUserProfileResult
    fun getProfileNotifications(): Flow<PagingData<ProfileNotificationEntity>>
    suspend fun fetchHasUnreadNotificationsNotifications(): GetUserUnreadNotificationsResult
    fun updateUserImage(fileUri: Uri)
    suspend fun getCountries(): List<CountryEntity>
}
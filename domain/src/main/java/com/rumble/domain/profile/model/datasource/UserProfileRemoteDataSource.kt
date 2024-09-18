package com.rumble.domain.profile.model.datasource

import android.net.Uri
import androidx.paging.PagingData
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.domain.domainmodel.GetUserProfileResult
import com.rumble.domain.settings.domain.domainmodel.GetUserUnreadNotificationsResult
import com.rumble.domain.settings.domain.domainmodel.UpdateUserProfileResult
import kotlinx.coroutines.flow.Flow

interface UserProfileRemoteDataSource {
    suspend fun fetchUserProfile(): GetUserProfileResult
    suspend fun updateUserProfile(userProfileEntity: UserProfileEntity): UpdateUserProfileResult
    fun fetchProfileNotifications(): Flow<PagingData<ProfileNotificationEntity>>
    suspend fun fetchHasUnreadNotificationsNotifications(): GetUserUnreadNotificationsResult
    fun updateUserImage(fileUri: Uri)
}
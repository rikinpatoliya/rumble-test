package com.rumble.domain.profile.model.repository

import android.net.Uri
import androidx.paging.PagingData
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.profile.model.datasource.UserProfileLocalDataSource
import com.rumble.domain.profile.model.datasource.UserProfileRemoteDataSource
import com.rumble.domain.settings.domain.domainmodel.GetUserUnreadNotificationsResult
import com.rumble.network.api.LoginApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProfileRepositoryImpl(
    private val loginApi: LoginApi,
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource,
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val dispatcher: CoroutineDispatcher,
) : ProfileRepository {

    override suspend fun signOut() {
        loginApi.signOut()
    }

    override suspend fun getUserProfile() =
        withContext(dispatcher) {
            userProfileRemoteDataSource.fetchUserProfile()
        }

    override suspend fun updateUserProfile(userProfileEntity: UserProfileEntity) =
        withContext(dispatcher) {
            userProfileRemoteDataSource.updateUserProfile(userProfileEntity)
        }

    override fun getProfileNotifications(): Flow<PagingData<ProfileNotificationEntity>> =
        userProfileRemoteDataSource.fetchProfileNotifications()

    override suspend fun fetchHasUnreadNotificationsNotifications(): GetUserUnreadNotificationsResult = withContext(dispatcher) {
        userProfileRemoteDataSource.fetchHasUnreadNotificationsNotifications()
    }

    override fun updateUserImage(fileUri: Uri) =
        userProfileRemoteDataSource.updateUserImage(fileUri)

    override suspend fun getCountries(): List<CountryEntity> =
        withContext(dispatcher) {
            userProfileLocalDataSource.getCountries()
        }
}
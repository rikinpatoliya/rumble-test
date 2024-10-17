package com.rumble.domain.profile.model.datasource

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingData
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.getRumblePagingConfig
import com.rumble.domain.feed.model.getUserProfileEntity
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.domain.domainmodel.GetUserProfileResult
import com.rumble.domain.settings.domain.domainmodel.GetUserUnreadNotificationsResult
import com.rumble.domain.settings.domain.domainmodel.UpdateUserProfileResult
import com.rumble.domain.uploadmanager.UploadManager
import com.rumble.network.api.UserApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import okhttp3.FormBody

private const val TAG = "UserProfileRemoteDataSourceImpl"

class UserProfileRemoteDataSourceImpl(
    private val userApi: UserApi,
    private val uploadManager: UploadManager,
    private val dispatcher: CoroutineDispatcher
) : UserProfileRemoteDataSource {
    override suspend fun fetchUserProfile(): GetUserProfileResult {
        val response = userApi.fetchUserProfile()
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            GetUserProfileResult(success = true, userProfileEntity = responseBody.data.getUserProfileEntity())
        else
            GetUserProfileResult(success = false, rumbleError = RumbleError(tag = TAG, response = response.raw()))
    }

    override suspend fun updateUserProfile(userProfileEntity: UserProfileEntity): UpdateUserProfileResult {
        val response = userApi.updateUserProfile(
            apiKey = userProfileEntity.apiKey,
            addressBody = FormBody.Builder()
                .add("fullname", userProfileEntity.fullName)
                .add("phone", userProfileEntity.phone)
                .add("address1", userProfileEntity.address)
                .add("address2", "")
                .add("city", userProfileEntity.city)
                .add("stateprov", userProfileEntity.state)
                .add("postalcode", userProfileEntity.postalCode)
                /*TODO uncomment once age verification is added back*/
//                .add("birthday_year", userProfileEntity.birthday?.year?.toString() ?: "")
//                .add("birthday_month", userProfileEntity.birthday?.monthValue?.toString() ?: "")
//                .add("birthday_day", userProfileEntity.birthday?.dayOfMonth?.toString() ?: "")
                .add("gender", userProfileEntity.gender.requestValue)
                .add("countryID", userProfileEntity.country.countryID.toString())
                .apply {
                    if (userProfileEntity.paypalEmail.isNotEmpty())
                        this.add("payinfo", userProfileEntity.paypalEmail)
                }
                .build()
        )
        val body = response.body()
        return if (response.isSuccessful && body != null)
            if (body.success) {
                UpdateUserProfileResult.Success(requiresConfirmation = body.requiresConfirmation)
            } else {
                UpdateUserProfileResult.FormError(
                    fullNameError = body.fullNameErrorMessage != null,
                    cityError = body.cityErrorMessage != null,
                    stateError = body.stateErrorMessage != null,
                    postalCodeError = body.postalCodeErrorMessage != null,
                    fullNameErrorMessage = body.fullNameErrorMessage ?: "",
                    cityErrorMessage = body.cityErrorMessage ?: "",
                    stateErrorMessage = body.stateErrorMessage ?: "",
                    birthdayErrorMessage = body.birthdayErrorMessage ?: "",
                    postalCodeErrorMessage = body.postalCodeErrorMessage ?: ""
                )
            }
        else
            UpdateUserProfileResult.Error(rumbleError = RumbleError(tag = TAG, response = response.raw()))
    }

    override fun fetchProfileNotifications(): Flow<PagingData<ProfileNotificationEntity>> {
        return Pager(
            config = getRumblePagingConfig(),
            pagingSourceFactory = {
                ProfileNotificationsPagingSource(
                    userApi = userApi,
                    dispatcher = dispatcher,
                )
            }).flow
    }

    override suspend fun fetchHasUnreadNotificationsNotifications(): GetUserUnreadNotificationsResult {
        val response = userApi.fetchUnreadNotifications()
        val hasUnreadNotifications = response.body()?.data?.hasUnreadNotifications
        return if (response.isSuccessful && hasUnreadNotifications != null)
            GetUserUnreadNotificationsResult(
                success = true,
                hasUnreadNotifications = hasUnreadNotifications
            )
        else
            GetUserUnreadNotificationsResult(
                success = false,
                rumbleError = RumbleError(tag = TAG, response = response.raw())
            )
    }

    override fun updateUserImage(fileUri: Uri) = uploadManager.uploadUserImage(fileUri)
}
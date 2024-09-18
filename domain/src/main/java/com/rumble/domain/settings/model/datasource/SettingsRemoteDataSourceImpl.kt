package com.rumble.domain.settings.model.datasource

import com.rumble.battles.network.BuildConfig
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.model.getNotificationSettingsEntity
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.settings.domain.domainmodel.AuthProviderEntity
import com.rumble.domain.settings.domain.domainmodel.AuthProvidersResult
import com.rumble.domain.settings.domain.domainmodel.CanSubmitLogsResult
import com.rumble.domain.settings.domain.domainmodel.CloseAccountResult
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsResult
import com.rumble.domain.settings.domain.domainmodel.UpdateUserDetailsResult
import com.rumble.network.Environment
import com.rumble.network.api.UserApi
import com.rumble.network.dto.settings.AuthProviders
import com.rumble.network.dto.settings.UpdateNotificationsDataResponse
import okhttp3.FormBody

private const val TAG = "SettingsRemoteDataSourceImpl"

class SettingsRemoteDataSourceImpl(
    private val userApi: UserApi,
) : SettingsRemoteDataSource {

    override suspend fun fetchNotificationSettings(): NotificationSettingsResult {
        val response = userApi.fetchNotificationSettings()
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            NotificationSettingsResult(
                success = true,
                error = null,
                canUseCustomApiDomain = if (BuildConfig.ENVIRONMENT == Environment.QA) true else responseBody.user.debug?.canUseCustomApiDomain ?: false,
                notificationSettingsEntity = responseBody.data.getNotificationSettingsEntity()
            )
        else
            NotificationSettingsResult(
                success = false,
                error = "fetchNotificationSettings failed with code: ${response.code()} and errorBody: ${response.errorBody()}",
                canUseCustomApiDomain = false,
                notificationSettingsEntity = null
            )
    }

    override suspend fun fetchAuthProviders(): AuthProvidersResult {
        val response = userApi.fetchAuthProviders()
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null) {
            AuthProvidersResult(
                success = true,
                authProviderEntity = getAuthProviderEntity(responseBody.data)
            )
        } else {
            AuthProvidersResult(
                success = response.code() == 403,
                authProviderEntity = AuthProviderEntity(LoginType.UNKNOWN, false)
            )
        }
    }

    override suspend fun fetchCanSubmitLogs(): CanSubmitLogsResult {
        val response = userApi.fetchUserProfile()
        val body = response.body()
        return if (response.isSuccessful) {
            CanSubmitLogsResult.Success(body?.user?.debug?.canSubmitLogs == true)
        } else {
            CanSubmitLogsResult.Failure(RumbleError(tag = TAG, response = response.raw()))
        }
    }

    override suspend fun unlinkAuthProvider(loginType: LoginType): Boolean {
        val response = userApi.unlinkAuthProvider(
            unlinkAuthProviderBody = FormBody.Builder()
                .add(
                    "provider",
                    when (loginType) {
                        LoginType.FACEBOOK -> "facebook"
                        LoginType.GOOGLE -> "google"
                        LoginType.APPLE -> "apple"
                        else -> ""
                    }
                )
                .build()
        )
        return response.isSuccessful
    }

    private fun getAuthProviderEntity(data: AuthProviders): AuthProviderEntity {
        return if (data.apple.isConnected)
            AuthProviderEntity(LoginType.APPLE, data.apple.canUnlink)
        else if (data.facebook.isConnected)
            AuthProviderEntity(LoginType.FACEBOOK, data.facebook.canUnlink)
        else if (data.google.isConnected)
            AuthProviderEntity(LoginType.GOOGLE, data.google.canUnlink)
        else
            AuthProviderEntity(LoginType.UNKNOWN, false)
    }

    override suspend fun updateNotificationSettings(notificationSettingsEntity: NotificationSettingsEntity): NotificationSettingsResult {
        val response = userApi.updateNotificationSettings(
            notificationSettingsBody = FormBody.Builder()
                .add("media_approved", notificationSettingsEntity.legacyValues.mediaApproved.toString())
                .add("media_comment", notificationSettingsEntity.legacyValues.mediaComment.toString())
                .add("comment_replied", notificationSettingsEntity.legacyValues.commentReplied.toString())
                .add("battle_posted", notificationSettingsEntity.legacyValues.battlePosted.toString())
                .add("win_money", notificationSettingsEntity.legacyValues.winMoney.toString())
                .add("video_trending", notificationSettingsEntity.legacyValues.videoTrending.toString())
                .add("allow_push", notificationSettingsEntity.legacyValues.allowPush.toString())
                .add("earn", notificationSettingsEntity.moneyEarned.toString())
                .add("video_live", notificationSettingsEntity.videoApprovedForMonetization.toString())
                .add("follow", notificationSettingsEntity.someoneFollowsYou.toString())
                .add("tag", notificationSettingsEntity.someoneTagsYou.toString())
                .add("comment", notificationSettingsEntity.commentsOnYourVideo.toString())
                .add("comment_reply", notificationSettingsEntity.repliesToYourComments.toString())
                .add("new_video", notificationSettingsEntity.newVideoBySomeoneYouFollow.toString())
                .build()
        )
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            NotificationSettingsResult(
                success = responseBody.data.success,
                error = getError(responseBody.data),
                canUseCustomApiDomain = if (BuildConfig.ENVIRONMENT == Environment.QA) true else responseBody.user.debug?.canUseCustomApiDomain ?: false,
                notificationSettingsEntity = if (responseBody.data.success) notificationSettingsEntity else null
            )
        else {
            NotificationSettingsResult(
                success = false,
                error = "fetchNotificationSettings failed with code: ${response.code()} and errorBody: ${response.errorBody()}",
                canUseCustomApiDomain = false,
                notificationSettingsEntity = null
            )
        }
    }

    override suspend fun updateEmail(email: String, password: String): UpdateUserDetailsResult {
        val response = userApi.updateUserEmail(
            body = FormBody.Builder()
                .add("new_email", email)
                .add("current_password", password)
                .build()
        )
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null) {
            UpdateUserDetailsResult(responseBody.success, responseBody.message)
        } else {
            UpdateUserDetailsResult(success = false, rumbleError = RumbleError(tag = TAG, response = response.raw()))
        }
    }

    override suspend fun updatePassword(
        newPassword: String,
        currentPassword: String
    ): UpdateUserDetailsResult {
        val response = userApi.updateUserPassword(
            body = FormBody.Builder()
                .add("new_password", newPassword)
                .add("current_password", currentPassword)
                .build()
        )
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null) {
            UpdateUserDetailsResult(responseBody.success, responseBody.message)
        } else {
            UpdateUserDetailsResult(false)
        }
    }

    override suspend fun closeAccount(): CloseAccountResult {
        val response = userApi.closeAccount()
        return CloseAccountResult(
            success = response.isSuccessful,
            rumbleError = if (response.isSuccessful.not()) RumbleError(tag = TAG, response = response.raw()) else null
        )
    }

    private fun getError(responseBody: UpdateNotificationsDataResponse): String? {
        return if (!responseBody.error.isNullOrEmpty()) responseBody.error else responseBody.message
    }
}
package com.rumble.network.api

import com.rumble.network.dto.channel.UploadChannelsResponse
import com.rumble.network.dto.profile.GetProfileNotificationsResponse
import com.rumble.network.dto.profile.GetUnreadNotificationsResponse
import com.rumble.network.dto.profile.ProfileResponse
import com.rumble.network.dto.profile.UpdateProfileResponse
import com.rumble.network.dto.profile.UpdateUserImageResponse
import com.rumble.network.dto.referral.ReferralsResponse
import com.rumble.network.dto.settings.AuthProvidersResponse
import com.rumble.network.dto.settings.Earnings
import com.rumble.network.dto.settings.NotificationSettingsResponse
import com.rumble.network.dto.settings.UpdateNotificationSettingsResponse
import com.rumble.network.dto.settings.UpdateUserEmailResponse
import com.rumble.network.dto.settings.UpdateUserPasswordResponse
import okhttp3.FormBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApi {

    @GET("service.php?name=user.profile")
    suspend fun fetchUserProfile(): Response<ProfileResponse>

    @POST("account/address")
    suspend fun updateUserProfile(
        @Query("a") id: String = "updateaddress",
        @Query("apiKey") apiKey: String,
        @Body addressBody: FormBody
    ): Response<UpdateProfileResponse>

    @GET("service.php?name=user.notification_feed")
    suspend fun fetchProfileNotifications(
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<GetProfileNotificationsResponse>

    @GET("service.php?name=user.has_unread_notifications")
    suspend fun fetchUnreadNotifications(): Response<GetUnreadNotificationsResponse>

    @Multipart
    @POST("api/User/SetProfilePicture")
    suspend fun updateUserImage(
        @Part body: MultipartBody.Part,
        @Part profileImage: MultipartBody.Part
    ): Response<UpdateUserImageResponse>

    /**
     * Change the user’s email
     */
    @POST("api/User/ChangeEmail")
    suspend fun updateUserEmail(
        @Body body: FormBody
    ): Response<UpdateUserEmailResponse>

    /**
     * Change the user’s password
     */
    @POST("api/User/ChangePassword")
    suspend fun updateUserPassword(
        @Body body: FormBody
    ): Response<UpdateUserPasswordResponse>

    /**
     * Initiates the user's account deletion flow by requesting an email with the account deactivation link.
     */
    @POST("service.php?name=user.send-deactivation-link")
    suspend fun closeAccount(): Response<Any>

    /**
     * Return user in-app notifications settings
     */
    @GET("service.php?name=user.notifications")
    suspend fun fetchNotificationSettings(): Response<NotificationSettingsResponse>

    @POST("service.php?name=user.notifications")
    suspend fun updateNotificationSettings(
        @Body notificationSettingsBody: FormBody
    ): Response<UpdateNotificationSettingsResponse>

    @GET("service.php?name=user.referrals")
    suspend fun getReferrals(): Response<ReferralsResponse>

    /**
     * Get a list of auth providers available for the user
     */
    @GET("service.php?name=user.get-auth-provider-list")
    suspend fun fetchAuthProviders(): Response<AuthProvidersResponse>

    /**
     * Unlinks the user from an auth provider
     */
    @POST("service.php?name=user.unlink-auth-provider")
    suspend fun unlinkAuthProvider(
        @Body unlinkAuthProviderBody: FormBody,
    ): Response<Any>

    @GET("/account/overview?mobile=1")
    suspend fun fetchEarnings(): Response<Earnings>

    @GET("service.php?name=user.upload-channels")
    suspend fun fetchUploadChannels(): Response<UploadChannelsResponse>

    /**
     * Request verification email be sent to user's email.
     *
     * @param body
     * e: user's email
     * loggedIn: 1
     */
    @POST("/api/User/ForgotActivation")
    suspend fun requestEmailVerification(
        @Body body: FormBody,
    ): Response<Any>

    @POST("service.php?name=user.expire_other_sessions")
    suspend fun expireSessions(): Response<Any>
}
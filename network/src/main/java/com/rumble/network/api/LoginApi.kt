package com.rumble.network.api

import com.rumble.network.dto.login.FacebookLoginResponse
import com.rumble.network.dto.login.GoogleAppleResponse
import com.rumble.network.dto.login.RegisterResponse
import com.rumble.network.dto.login.ResetPasswordResponse
import com.rumble.network.dto.login.RumbleLoginResponse
import com.rumble.network.dto.login.TvPairingCodeResponse
import com.rumble.network.dto.login.TvPairingCodeVerificationResponse
import okhttp3.FormBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {

    @GET("login.php")
    suspend fun fetchPasswordSalts(@Query("gus") userName: String): List<String>

    @POST("login.php")
    suspend fun rumbleLogin(@Body loginBody: FormBody): Response<RumbleLoginResponse>

    /**
     * Request a TV device pairing code
     *
     * The request is used to retrieve a unique, generated code for pairing a TV device to a user
     * account. To be used in conjunction with https://rumble.com/pair and roku.link.code_verify.
     *
     * When retryDuration time is running out, the client should request another code slightly in advance.
     *
     * @param body HTTP Body, application/x-www-form-urlencoded, containing the following parameters:
     *                  deviceID=<String> // A unique identifier of the device or the installation
     */
    @POST("service.php?name=roku.link.code_get")
    suspend fun requestTvPairingCode(@Body body: FormBody): Response<TvPairingCodeResponse>

    /**
     * Verify a TV device pairing code
     *
     * The request is to check whether the code has been successfully paired with a user account.
     * A TV device should be polling the backend every couple of seconds, watching the "status"
     * field of the response. The first request should be sent no earlier than a couple of seconds
     * after the code was presented in the UI.
     * If a native OS mechanism (HTTP functionality) will be used to handle the cookie, make sure
     * to take that into account for all other responses, as the cookie TTL will be extended by the
     * server by sending a corresponding header in other API responses from time to time.
     *
     * Along with the successful response, session authorization cookie u_s is sent in headers.
     *
     * @param body HTTP Body, application/x-www-form-urlencoded, containing the following parameters:
     *             regCode=<String> // A code that was received from roku.link.code_get
     */
    @POST("service.php?name=roku.link.code_verify")
    suspend fun verifyTvPairingCode(@Body body: FormBody): Response<TvPairingCodeVerificationResponse>

    @POST("service.php")
    suspend fun googleAppleLogin(
        @Body loginBody: FormBody,
        @Query("name") name: String
    ): Response<GoogleAppleResponse>

    @POST("register.php")
    suspend fun facebookRumbleRegister(
        @Body body: FormBody,
        @Query("a") provider: String,
        @Query("api") api: String = "6",
    ): Response<RegisterResponse>

    @POST("service.php")
    suspend fun googleAppleRegister(
        @Body body: FormBody,
        @Query("name") provider: String,
        @Query("api") api: String = "6",
    ): Response<RegisterResponse>

    @GET("api/User/LoginFacebook")
    suspend fun facebookLogin(
        @Query("m_user_id") userId: String,
        @Query("m_access_token") accessToken: String,
    ): Response<FacebookLoginResponse>

    /**
     * Notify the server that the user has logged out.
     * The request is “fire and forget”. Ignore any responses or errors.
     */
    @GET("service.php?name=user.logout")
    suspend fun signOut()

    @POST("service.php?name=user.password_forgot")
    suspend fun resetPassword(@Body body: FormBody): Response<ResetPasswordResponse>
}
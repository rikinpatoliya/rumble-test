package com.rumble.domain.login.model

import com.rumble.domain.common.domain.domainmodel.TvPairingCodeResult
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.login.domain.domainmodel.LoginResult
import com.rumble.domain.login.domain.domainmodel.LoginResultStatus
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.login.domain.domainmodel.RegisterResult
import com.rumble.domain.login.domain.domainmodel.ResetPasswordResult
import com.rumble.domain.login.model.datasource.LoginRemoteDataSource
import com.rumble.network.NetworkRumbleConstants.TOO_MANY_REQUESTS
import com.rumble.network.api.LoginApi
import com.rumble.network.dto.login.FacebookLoginResponse
import com.rumble.network.dto.login.GoogleAppleResponse
import com.rumble.network.dto.login.RegisterErrorResponse
import com.rumble.network.dto.login.RumbleLoginResponse
import com.rumble.network.dto.login.TvPairingCodeVerificationDataStatus
import com.rumble.network.dto.login.TvPairingCodeVerificationResponse
import com.rumble.network.getResponseResult
import com.rumble.utils.HashCalculator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "LoginRepositoryImpl"

class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val hashCalculator: HashCalculator,
    private val registerErrorConverter: Converter<ResponseBody, RegisterErrorResponse>?,
) : LoginRepository {

    override suspend fun rumbleLogin(username: String, password: String): LoginResult =
        withContext(dispatcher) {
            val salts = loginApi.fetchPasswordSalts(username)
            val hash = hashCalculator.calculateHashStretched(salts, password)
            val response = loginApi
                .rumbleLogin(FormBody.Builder().add("u", username).add("p", hash).build())
            createLoginResult(response)
        }

    override suspend fun ssoLogin(
        loginType: LoginType,
        userId: String,
        token: String
    ): LoginResult =
        withContext(dispatcher) {
            val response: Response<*> = when (loginType) {
                LoginType.FACEBOOK -> loginApi.facebookLogin(userId, token)
                LoginType.GOOGLE -> loginApi.googleAppleLogin(
                    createGoogleAppleLoginFormBody(loginType, userId, token),
                    "user.login.google"
                )

                LoginType.APPLE -> loginApi.googleAppleLogin(
                    createGoogleAppleLoginFormBody(loginType, userId, token),
                    "user.login.apple"
                )

                else -> throw Error("Unsupported SSO login type!")
            }
            createLoginResult(response)
        }

    private fun createGoogleAppleLoginFormBody(
        loginType: LoginType,
        userId: String,
        token: String
    ) = FormBody.Builder().add("user_id", userId).add("jwt", token)
        .add("provider", if (loginType == LoginType.GOOGLE) "google" else "apple").build()

    override suspend fun register(
        loginType: LoginType,
        body: FormBody
    ): RegisterResult =
        withContext(dispatcher) {
            val response = when (loginType) {
                LoginType.GOOGLE, LoginType.APPLE -> loginApi.googleAppleRegister(
                    body = body,
                    provider = loginType.provider
                )

                else -> loginApi.facebookRumbleRegister(
                    body = body,
                    provider = loginType.provider
                )
            }
            val responseResult = response.getResponseResult(registerErrorConverter)
            if (responseResult.first) RegisterResult.Success
            else if (response.code() == TOO_MANY_REQUESTS) {
                RegisterResult.DuplicatedRequest(RumbleError(TAG, response.raw()))
            } else RegisterResult.Failure(RumbleError(TAG, response.raw()), responseResult.second)
        }

    override suspend fun requestTvPairingCode(deviceId: String): TvPairingCodeResult {
        val response = loginApi.requestTvPairingCode(
            FormBody
                .Builder()
                .add("deviceID", deviceId)
                .build()
        )

        return if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            TvPairingCodeResult.Success(body.data)
        } else {
            TvPairingCodeResult.Failure(
                RumbleError(
                    tag = TAG,
                    response = response.raw(),
                    customMessage = "deviceId:$deviceId"
                )
            )
        }
    }

    override suspend fun verifyTvPairingCode(regCode: String): LoginResult =
        withContext(dispatcher) {
            val response = loginApi.verifyTvPairingCode(
                FormBody
                    .Builder()
                    .add("regCode", regCode)
                    .build()
            )

            if (response.isSuccessful && response.body() != null) {
                createLoginResult(response)
            } else {
                LoginResult(false, status = LoginResultStatus.FAILURE)
            }
        }

    override suspend fun resetPassword(emailOrUsername: String): ResetPasswordResult {
        return loginRemoteDataSource.resetPassword(emailOrUsername)
    }

    private fun extractCookies(response: Response<*>) = if (response.isSuccessful) {
        response.headers().values("set-cookie").joinToString("")
    } else null

    private fun createLoginResult(response: Response<*>): LoginResult {
        return when (val body = response.body()) {
            is GoogleAppleResponse -> {
                val cookies = extractCookies(response)
                val loginSuccess = body.userData.success
                val error = body.userData.error
                val userId = body.userData.userId
                val userName = body.userData.userName
                val userThumbnail = body.userData.thumb
                LoginResult(loginSuccess, error, cookies, userId, userName, userThumbnail)
            }

            is RumbleLoginResponse -> {
                val cookies = extractCookies(response)
                val loginSuccess = body.success > 0
                val userId = body.userId
                val userName = body.userName
                val userThumbnail = body.profilePicture
                LoginResult(loginSuccess, null, cookies, userId, userName, userThumbnail)
            }

            is TvPairingCodeVerificationResponse -> {
                val cookies = extractCookies(response)
                val loginSuccess = body.data.status == TvPairingCodeVerificationDataStatus.SUCCESS
                val userId = body.data.user?.id
                val userName = if (loginSuccess) body.data.user?.userName else ""
                val userThumbnail = if (loginSuccess) body.data.user?.profilePic?.default else ""
                LoginResult(
                    loginSuccess,
                    body.data.message,
                    cookies,
                    userId = userId,
                    userName,
                    userThumbnail,
                    LoginResultStatus.get(body.data.status)
                )

            }

            is FacebookLoginResponse -> {
                val cookies = extractCookies(response)
                val loginSuccess = ((body.returnData.success as? Int)
                    ?: 0) > 0 || (body.returnData.success as? Boolean ?: false)
                val userId = body.returnData.userData?.userId
                val userName = body.returnData.userData?.userName
                LoginResult(loginSuccess, null, cookies, userId, userName, null)
            }

            else -> LoginResult(success = false, rumbleError = RumbleError(TAG, response.raw()))
        }
    }
}
package com.rumble.domain.login.model.datasource

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.login.domain.domainmodel.ResetPasswordResult
import com.rumble.network.api.LoginApi
import okhttp3.FormBody

const val TAG = "LoginRemoteDataSourceImpl"

class LoginRemoteDataSourceImpl(private val loginApi: LoginApi) :
    LoginRemoteDataSource {
    override suspend fun resetPassword(emailOrUsername: String): ResetPasswordResult {
        val body = FormBody.Builder()
            .add("email", emailOrUsername)
            .build()

        val response = loginApi.resetPassword(body = body)

        return if (response.isSuccessful) {
            val resetPasswordResponse = response.body()
            if (resetPasswordResponse?.data?.error == null) {
                ResetPasswordResult.Success
            } else {
                ResetPasswordResult.Failure(
                    RumbleError(
                        tag = TAG,
                        response = response.raw(),
                        customMessage = resetPasswordResponse.data.error ?: ""
                    )
                )
            }
        } else {
            ResetPasswordResult.Failure(RumbleError(TAG, response.raw()))
        }
    }
}
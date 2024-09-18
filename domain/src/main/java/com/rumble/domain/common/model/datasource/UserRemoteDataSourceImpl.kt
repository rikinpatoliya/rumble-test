package com.rumble.domain.common.model.datasource

import com.rumble.domain.common.domain.domainmodel.EmptyResult
import com.rumble.domain.common.model.RumbleError
import com.rumble.network.api.UserApi
import okhttp3.FormBody
import javax.inject.Inject

private const val TAG = "UserRemoteDataSourceImpl"

class UserRemoteDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
) : UserRemoteDataSource {
    override suspend fun requestVerificationEmail(email: String): EmptyResult {
        val body = FormBody.Builder()
            .add("e", email)
            .add("loggedIn", "1")
            .build()

        val response = userApi.requestEmailVerification(body)

        return if (response.isSuccessful) {
            EmptyResult.Success
        } else {
            EmptyResult.Failure(
                rumbleError = RumbleError(TAG, response = response.raw())
            )
        }
    }
}
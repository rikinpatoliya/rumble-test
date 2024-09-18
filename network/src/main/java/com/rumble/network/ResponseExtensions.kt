package com.rumble.network

import com.rumble.network.dto.login.RegisterResponse
import retrofit2.Response

fun Response<RegisterResponse>.getResponseResult(): Pair<Boolean, String?> {
    val responseBody = this.body()
    return if (this.isSuccessful && responseBody != null) {
        responseBody.registerData?.let { data ->
            Pair(data.success, data.getFirstError())
        } ?: kotlin.run {
            Pair(responseBody.success, responseBody.getFirstError())
        }
    } else {
        Pair(false, null)
    }
}

/**
 * App environments.
 */
interface Environment {
    companion object {
        const val PROD = "PROD"

        const val QA = "QA"

        const val DEV = "DEV"
    }
}
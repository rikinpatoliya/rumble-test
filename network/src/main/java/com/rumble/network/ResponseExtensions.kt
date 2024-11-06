package com.rumble.network

import com.rumble.network.dto.login.RegisterErrorResponse
import com.rumble.network.dto.login.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response

fun Response<RegisterResponse>.getResponseResult(
    registerErrorConverter: Converter<ResponseBody, RegisterErrorResponse>?
): Pair<Boolean, String?> {
    val responseBody = this.body()
    return if (this.isSuccessful && responseBody != null) {
        responseBody.registerData?.let { data ->
            Pair(data.success, data.getFirstError())
        } ?: kotlin.run {
            Pair(responseBody.success, responseBody.getFirstError())
        }
    } else {
        this.errorBody()?.let { errorBody ->
            val error = registerErrorConverter?.convert(errorBody)
            val errorMessage = error?.errors?.firstOrNull()?.message
            Pair(false, errorMessage)
        } ?: kotlin . run {
            Pair(false, null)
        }
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
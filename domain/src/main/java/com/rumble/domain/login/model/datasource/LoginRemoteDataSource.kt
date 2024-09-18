package com.rumble.domain.login.model.datasource

import com.rumble.domain.login.domain.domainmodel.ResetPasswordResult

interface LoginRemoteDataSource {
    suspend fun resetPassword(emailOrUsername: String): ResetPasswordResult
}
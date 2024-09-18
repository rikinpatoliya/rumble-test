package com.rumble.domain.login.model

import com.rumble.domain.common.domain.domainmodel.TvPairingCodeResult
import com.rumble.domain.login.domain.domainmodel.LoginResult
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.login.domain.domainmodel.RegisterResult
import com.rumble.domain.login.domain.domainmodel.ResetPasswordResult
import okhttp3.FormBody

interface LoginRepository {
    suspend fun rumbleLogin(username: String, password: String): LoginResult
    suspend fun ssoLogin(loginType: LoginType, userId: String, token: String): LoginResult
    suspend fun register(loginType: LoginType, body: FormBody): RegisterResult
    suspend fun requestTvPairingCode(deviceId: String): TvPairingCodeResult
    suspend fun verifyTvPairingCode(regCode: String): LoginResult
    suspend fun resetPassword(emailOrUsername: String): ResetPasswordResult
}
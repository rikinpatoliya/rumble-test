package com.rumble.domain.login.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.landing.usecases.AppsFlySetUserIdUseCase
import com.rumble.domain.landing.usecases.ExtractCookiesUseCase
import com.rumble.domain.landing.usecases.OneSignalExternalUserIdUseCase
import com.rumble.domain.landing.usecases.OneSignalPushNotificationEnableUseCase
import com.rumble.domain.landing.usecases.SetOneSignalUserPremiumTagUseCase
import com.rumble.domain.login.domain.domainmodel.LoginResult
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.login.model.LoginRepository
import com.rumble.network.session.SessionManager
import com.rumble.utils.extension.toUserIdString
import javax.inject.Inject

class SSOLoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val sessionManager: SessionManager,
    private val extractCookiesUseCase: ExtractCookiesUseCase,
    private val oneSignalExternalUserIdUseCase: OneSignalExternalUserIdUseCase,
    private val oneSignalPushNotificationEnableUseCase: OneSignalPushNotificationEnableUseCase,
    private val setOneSignalUserPremiumTagUseCase: SetOneSignalUserPremiumTagUseCase,
    private val appsFlySetUserIdUseCase: AppsFlySetUserIdUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(
        loginType: LoginType,
        userId: String,
        token: String,
    ): LoginResult {
        val result = loginRepository.ssoLogin(loginType, userId, token)
        if (result.success) {
            result.cookie?.let { sessionManager.saveUserCookies(extractCookiesUseCase(it)) }
            result.userId?.let {
                sessionManager.saveUserId(it.toUserIdString())
                oneSignalExternalUserIdUseCase(it)
                oneSignalPushNotificationEnableUseCase(true)
                appsFlySetUserIdUseCase(it.toUserIdString())
            }
            result.userName?.let { sessionManager.saveUserName(it) }
            if (loginType == LoginType.GOOGLE)
                result.userPicture?.let { sessionManager.saveUserPicture(it) }
            sessionManager.saveLoginType(loginType.value)
            setOneSignalUserPremiumTagUseCase(true)
        } else {
            result.rumbleError?.let {
                rumbleErrorUseCase(it)
            }
        }
        return result
    }
}
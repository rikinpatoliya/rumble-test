package com.rumble.domain.login.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.landing.usecases.AppsFlySetUserIdUseCase
import com.rumble.domain.landing.usecases.ExtractCookiesUseCase
import com.rumble.domain.landing.usecases.OneSignalExternalUserIdUseCase
import com.rumble.domain.landing.usecases.SetOneSignalUserPremiumTagUseCase
import com.rumble.domain.landing.usecases.SetUserPropertiesUseCase
import com.rumble.domain.login.domain.domainmodel.LoginResult
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.login.model.LoginRepository
import com.rumble.network.session.SessionManager
import com.rumble.utils.extension.toUserIdString
import javax.inject.Inject

class RumbleLoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val sessionManager: SessionManager,
    private val extractCookiesUseCase: ExtractCookiesUseCase,
    private val oneSignalExternalUserIdUseCase: OneSignalExternalUserIdUseCase,
    private val setOneSignalUserPremiumTagUseCase: SetOneSignalUserPremiumTagUseCase,
    private val appsFlySetUserIdUseCase: AppsFlySetUserIdUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
    private val setUserPropertiesUseCase: SetUserPropertiesUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): LoginResult {
        val result = loginRepository.rumbleLogin(username, password)
        if (result.success) {
            result.cookie?.let { sessionManager.saveUserCookies(extractCookiesUseCase(it)) }
            result.userId?.let {
                val base36UserId = it.toUserIdString()
                sessionManager.saveUserId(base36UserId)
                oneSignalExternalUserIdUseCase(it)
                appsFlySetUserIdUseCase(base36UserId)
                setUserPropertiesUseCase(base36UserId, true)
            }
            result.userName?.let { sessionManager.saveUserName(it) }
            result.userPicture?.let { sessionManager.saveUserPicture(it) }
            sessionManager.savePassword(password)
            sessionManager.saveLoginType(LoginType.RUMBLE.value)
            setOneSignalUserPremiumTagUseCase(true)
        } else {
            result.rumbleError?.let {
                rumbleErrorUseCase(it)
            }
        }
        return result
    }
}
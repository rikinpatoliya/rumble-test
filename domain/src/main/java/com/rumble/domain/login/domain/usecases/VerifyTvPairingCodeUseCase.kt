package com.rumble.domain.login.domain.usecases

import com.rumble.domain.landing.usecases.AppsFlySetUserIdUseCase
import com.rumble.domain.landing.usecases.ExtractCookiesUseCase
import com.rumble.domain.landing.usecases.OneSignalExternalUserIdUseCase
import com.rumble.domain.login.domain.domainmodel.LoginResultStatus
import com.rumble.domain.login.model.LoginRepository
import com.rumble.network.session.SessionManager
import com.rumble.utils.extension.toUserIdString
import javax.inject.Inject

class VerifyTvPairingCodeUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val sessionManager: SessionManager,
    private val extractCookiesUseCase: ExtractCookiesUseCase,
    private val oneSignalExternalUserIdUseCase: OneSignalExternalUserIdUseCase,
    private val appsFlySetUserIdUseCase: AppsFlySetUserIdUseCase,
) {
    suspend operator fun invoke(regCode : String): LoginResultStatus {
        val result = loginRepository.verifyTvPairingCode(regCode)

        if (result.success) {
            result.cookie?.let { sessionManager.saveUserCookies(extractCookiesUseCase(it)) }
            result.userId?.let {
                sessionManager.saveUserId(it.toUserIdString())
                oneSignalExternalUserIdUseCase(it)
                appsFlySetUserIdUseCase(it.toUserIdString())
            }
            result.userName?.let { sessionManager.saveUserName(it) }
            result.userPicture?.let { sessionManager.saveUserPicture(it) }
        }

        return result.status
    }
}
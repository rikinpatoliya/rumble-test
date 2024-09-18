package com.rumble.domain.login.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.TvPairingCodeResult
import com.rumble.domain.login.model.LoginRepository
import com.rumble.utils.DeviceUtil
import javax.inject.Inject

class RequestTvPairingCodeUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val deviceUtil: DeviceUtil,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(): TvPairingCodeResult {
        val result = loginRepository.requestTvPairingCode(deviceUtil.getAndroidId())

        if (result is TvPairingCodeResult.Failure) {
            rumbleErrorUseCase.invoke(result.rumbleError)
        }

        return result
    }
}
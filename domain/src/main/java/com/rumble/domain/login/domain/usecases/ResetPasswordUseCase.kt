package com.rumble.domain.login.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.login.model.LoginRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(emailOrUsername: String) =
        loginRepository.resetPassword(emailOrUsername)

}
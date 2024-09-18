package com.rumble.domain.login.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.login.domain.domainmodel.RegisterResult
import com.rumble.domain.login.model.LoginRepository
import okhttp3.FormBody
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(loginType: LoginType, body: FormBody): RegisterResult {
        val result = loginRepository.register(loginType, body)
        if (result is RegisterResult.Failure) {
            rumbleErrorUseCase(result.error)
        } else if (result is RegisterResult.DuplicatedRequest) {
            rumbleErrorUseCase(result.error)
        }
        return result
    }
}
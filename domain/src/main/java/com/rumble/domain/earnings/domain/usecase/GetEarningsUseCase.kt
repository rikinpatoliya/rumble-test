package com.rumble.domain.earnings.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.earnings.domainmodel.EarningsResult
import com.rumble.domain.earnings.repository.EarningsRepository
import javax.inject.Inject

class GetEarningsUseCase @Inject constructor(
    private val earningsRepository: EarningsRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(): EarningsResult {
        val result = earningsRepository.fetchEarnings()
        if (result is EarningsResult.Failure) {
            rumbleErrorUseCase.invoke(result.rumbleError)
        }
        return result
    }
}
package com.rumble.domain.rumbleads.domain.usecase

import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.rumbleads.domain.domainmodel.SendAdEventResult
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepository
import javax.inject.Inject

class SendAdEventUseCase @Inject constructor(
    private val rumbleAdRepository: RumbleAdRepository,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
)  {
    suspend operator fun invoke(urlList: List<String>, initTime: Long) {
        val result = rumbleAdRepository.sendAdEvent(urlList, initTime)
        if (result is SendAdEventResult.UncaughtError) {
            unhandledErrorUseCase(result.tag, result.error)
        }
    }
}
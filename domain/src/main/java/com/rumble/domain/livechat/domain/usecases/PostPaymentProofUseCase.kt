package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.livechat.domain.domainmodel.PaymentProofResult
import com.rumble.domain.livechat.domain.domainmodel.PendingMessageInfo
import com.rumble.domain.livechat.model.repository.LiveChatRepository
import com.rumble.network.di.AppFlyerId
import com.rumble.network.di.AppId
import javax.inject.Inject

class PostPaymentProofUseCase @Inject constructor(
    private val liveChatRepository: LiveChatRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
    @AppId val appId: String,
    @AppFlyerId val appsFlyerId: String
) : RumbleUseCase {
    suspend operator fun invoke(
        pendingMessageInfo: PendingMessageInfo,
        token: String
    ): PaymentProofResult {
        val result = liveChatRepository.postPaymentProof(pendingMessageInfo, token, appId, appsFlyerId)
        if (result is PaymentProofResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}
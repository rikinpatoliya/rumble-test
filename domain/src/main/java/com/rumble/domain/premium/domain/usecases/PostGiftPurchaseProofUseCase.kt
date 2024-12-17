package com.rumble.domain.premium.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.premium.domain.domainmodel.PurchaseResult
import com.rumble.domain.premium.model.repository.PurchaseRepository
import com.rumble.network.di.AppId
import javax.inject.Inject

class PostGiftPurchaseProofUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
    @AppId val appId: String,
) : RumbleUseCase {

    suspend operator fun invoke(
        productId: String,
        purchaseToken: String,
        videoId: Long,
        channelId: Long?,
    ): PurchaseResult {
        val result = purchaseRepository.purchaseGift(
            productId = productId,
            purchaseToken = purchaseToken,
            appId = appId,
            videoId = videoId,
            channelId = channelId,
        )
        if (result is PurchaseResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}
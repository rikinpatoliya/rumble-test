package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.livechat.domain.domainmodel.GiftPopupMessageEntity
import com.rumble.network.session.SessionManager
import javax.inject.Inject

class HandlePremiumGiftUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(giftPopupMessageEntity: GiftPopupMessageEntity?) {
        if (giftPopupMessageEntity != null) {
            sessionManager.saveIsPremiumUser(true)
        }
    }
}
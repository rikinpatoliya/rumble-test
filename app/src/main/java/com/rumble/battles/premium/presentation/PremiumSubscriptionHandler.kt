package com.rumble.battles.premium.presentation

import androidx.compose.runtime.State
import com.rumble.domain.billing.domain.domainmodel.PurchaseType
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscriptionData
import com.rumble.network.queryHelpers.SubscriptionSource

interface PremiumSubscriptionHandler {
    val subscriptionList: List<PremiumSubscriptionData>
    val subscriptionUiState: State<SubscriptionUIState>

    fun onShowPremiumPromo(videoId: Long?, source: SubscriptionSource?)
    fun onClosePremiumPromo()
    fun onGetPremium()
    fun onShowSubscriptionOptions(
        videoId: Long? = null,
        creatorId: String? = null,
        source: SubscriptionSource?
    )

    fun onSubscribe(premiumSubscriptionData: PremiumSubscriptionData)
}

data class SubscriptionUIState(
    val plan: String = "",
    val memberSinceDate: String = "",
    val expirationDate: String = "",
    val paymentMethod: String = "",
    val isSubscribedFromApple: Boolean = false,
    val purchaseType: PurchaseType = PurchaseType.None,
)